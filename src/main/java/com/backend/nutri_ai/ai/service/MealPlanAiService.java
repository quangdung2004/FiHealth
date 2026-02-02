package com.backend.nutri_ai.ai.service;

import com.backend.nutri_ai.admin.entity.PromptTemplate;
import com.backend.nutri_ai.admin.repository.PromptTemplateRepository;
import com.backend.nutri_ai.ai.dto.CandidateDto;
import com.backend.nutri_ai.ai.dto.MealPlanAiOutput;
import com.backend.nutri_ai.ai.mapper.AiInteractionMapper;
import com.backend.nutri_ai.ai.repository.AiInteractionRepository;
import com.backend.nutri_ai.assessment.entity.BodyMetricsSnapshot;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.AiTask;
import com.backend.nutri_ai.common.enums.PlanPeriod;
import com.backend.nutri_ai.common.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanAiService {

    private final ChatModel chatModel;
    private final AiInteractionRepository aiRepo;
    private final AiInteractionMapper aiInteractionMapper;
    private final ObjectMapper objectMapper;
    private final PromptTemplateRepository promptTemplateRepository;

    public MealPlanAiOutput generate(
            AppUser user,
            NutritionAssessment assessment,
            BodyMetricsSnapshot metrics,
            PlanPeriod period,
            List<CandidateDto> candidates
    ) {

        String systemPrompt = defaultSystemPrompt();
        String userPrompt = defaultUserPrompt(buildInputJson(assessment, metrics, period, candidates));

        PromptTemplate tpl = promptTemplateRepository
                .findFirstByNameAndActiveTrueAndIsDefaultTrue("MEAL_PLAN_JSON")
                .orElse(null);

        if (tpl != null) {
            if (tpl.getSystemPrompt() != null && !tpl.getSystemPrompt().isBlank()) {
                systemPrompt = tpl.getSystemPrompt();
            }
            if (tpl.getUserPromptTemplate() != null && !tpl.getUserPromptTemplate().isBlank()) {
                userPrompt = tpl.getUserPromptTemplate()
                        .replace("{{inputJson}}", buildInputJson(assessment, metrics, period, candidates));
            }
        }

        Prompt prompt = new Prompt(
                new SystemMessage(systemPrompt),
                UserMessage.builder().text(userPrompt).build()
        );

        Instant start = Instant.now();
        String raw;

        try {
            raw = chatModel.call(prompt).getResult().getOutput().getText();

        } catch (RestClientResponseException e) {
            int status = e.getRawStatusCode();
            String msg = safePrefix(e.getResponseBodyAsString(), 300);

            if (status == 429) throw new AiQuotaExceededException();
            if (status == 403) throw new ForbiddenException("Không có quyền truy cập AI");
            if (status == 401) throw new UnauthorizedException("AI chưa xác thực");
            throw new ExternalApiErrorException("AI client error: " + status + " " + msg);

        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new AiTimeoutException("AI timeout");
            }
            throw new ExternalApiTimeoutException("Kết nối tới AI thất bại hoặc quá chậm");

        } catch (Exception e) {
            String m = (e.getMessage() != null) ? e.getMessage() : "";
            String lower = m.toLowerCase(Locale.ROOT);

            if (lower.contains("429") || lower.contains("too many") || lower.contains("rate limit") || lower.contains("quota")) {
                throw new AiQuotaExceededException("AI rate limit/quota exceeded");
            }
            if (lower.contains("timeout") || lower.contains("timed out")) {
                throw new AiTimeoutException("AI timeout");
            }
            if (lower.contains("401")) {
                throw new UnauthorizedException("AI unauthorized");
            }
            if (lower.contains("403")) {
                throw new ForbiddenException("AI access forbidden");
            }

            log.error("Unknown AI error. msg={}", m, e);
            throw new AiServiceUnavailableException("Dịch vụ AI tạm thời không khả dụng");
        }

        int latencyMs = (int) Duration.between(start, Instant.now()).toMillis();
        log.info("MealPlan AI latency={}ms", latencyMs);

        // logging interaction shouldn't break user flow (optional but recommended)
        try {
            aiRepo.save(aiInteractionMapper.toEntity(
                    user.getId(),
                    assessment.getId(),
                    AiTask.MEAL_PLAN_GENERATE,
                    systemPrompt + "\n\n" + userPrompt,
                    raw,
                    latencyMs,
                    "openrouter"
            ));
        } catch (Exception e) {
            log.warn("Failed to save AiInteraction log (ignored). msg={}", e.getMessage());
        }

        String json = extractJsonObject(raw);

        try {
            return objectMapper.readValue(json, MealPlanAiOutput.class);
        } catch (Exception ex) {
            log.error("Failed to parse meal plan JSON. rawStart='{}' json='{}'",
                    safePrefix(raw, 150), safePrefix(json, 1200));
            throw new AiResponseInvalidException();
        }
    }

    private String buildInputJson(NutritionAssessment a, BodyMetricsSnapshot m, PlanPeriod period, List<CandidateDto> cands) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("period", period.name());
            payload.put("mealsPerDay", a.getMealsPerDay());
            payload.put("budgetPerDayVnd", (a.getBudgetPerDayVnd() != null ? a.getBudgetPerDayVnd() : 0));
            payload.put("goal", a.getGoal().name());
            payload.put("calorieTarget", m.getCalorieTarget());
            payload.put("macroTarget", Map.of(
                    "proteinG", m.getProteinG(),
                    "fatG", m.getFatG(),
                    "carbG", m.getCarbG()
            ));
            payload.put("candidates", cands);
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            // đây là lỗi hệ thống nội bộ -> map về INTERNAL_ERROR
            throw new InternalErrorException("Failed to build AI input json");
        }
    }

    private String defaultSystemPrompt() {
        return """
Bạn là AI tạo thực đơn dinh dưỡng.
Chỉ được chọn món từ danh sách candidates (theo candidateId).
KHÔNG tự tạo món ngoài danh sách.
CHỈ trả về 1 JSON object hợp lệ đúng schema, không bọc ``` và không giải thích.

Quy tắc:
- Mỗi ngày phải có đúng mealsPerDay bữa.
- Tổng kcal/ngày gần calorieTarget (±10%).
- Tổng cost/ngày <= budgetPerDayVnd.
- servings > 0.
""";
    }

    private String defaultUserPrompt(String inputJson) {
        return """
Dữ liệu đầu vào (JSON):
%s

Trả về CHỈ MỘT JSON OBJECT hợp lệ theo schema:
{
  "period": "DAY|WEEK|MONTH",
  "days": [
    {
      "dayIndex": number,
      "meals": [
        {
          "mealOrder": number,
          "name": string,
          "items": [
            { "candidateId": string, "servings": number }
          ]
        }
      ]
    }
  ],
  "notes": string
}

Quy tắc:
- candidateId phải thuộc danh sách candidates.
- servings > 0.
- Mỗi ngày đúng mealsPerDay bữa.
- Tổng kcal/ngày gần calorieTarget (±10%%).
- Tổng cost/ngày <= budgetPerDayVnd.
- KHÔNG bọc bằng ``` và KHÔNG thêm chữ ngoài JSON.
""".formatted(inputJson);
    }

    private String extractJsonObject(String s) {
        if (s == null) return "{}";
        String t = s.trim();

        if (t.startsWith("```")) {
            int firstNewline = t.indexOf('\n');
            if (firstNewline > 0) t = t.substring(firstNewline + 1);
            int lastFence = t.lastIndexOf("```");
            if (lastFence >= 0) t = t.substring(0, lastFence);
            t = t.trim();
        }

        int start = t.indexOf('{');
        int end = t.lastIndexOf('}');
        if (start >= 0 && end > start) t = t.substring(start, end + 1).trim();

        if (!t.startsWith("{") || !t.endsWith("}")) return "{}";
        return t;
    }

    private String safePrefix(String s, int max) {
        if (s == null) return "";
        String t = s.replace("\n", "\\n").replace("\r", "\\r");
        return t.substring(0, Math.min(max, t.length()));
    }
}
