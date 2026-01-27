package com.backend.nutri_ai.ai.service;

import com.backend.nutri_ai.ai.dto.BodyImageAiDto;
import com.backend.nutri_ai.ai.mapper.AiInteractionMapper;
import com.backend.nutri_ai.ai.repository.AiInteractionRepository;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.AiTask;
import com.backend.nutri_ai.common.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class BodyImageAiService {

    private final ChatModel chatModel;
    private final AiInteractionRepository aiRepo;
    private final AiInteractionMapper aiInteractionMapper;
    private final ObjectMapper objectMapper;

    public BodyImageAiDto analyze(AppUser user, NutritionAssessment assessment, MultipartFile image) {

        String promptText = """
Bạn là AI phân tích hình thể (mang tính tham khảo, không chẩn đoán y khoa).

Trả về CHỈ MỘT JSON OBJECT HỢP LỆ theo schema:
{
  "bodyFatMin": number,
  "bodyFatMax": number,
  "postureNotes": string,
  "proportionsNotes": string,
  "safetyNotes": string
}

Quy tắc:
- safetyNotes luôn nhắc: "Chỉ tham khảo, không thay thế chuyên gia."
- KHÔNG được bọc bằng ``` hoặc ```json
- KHÔNG thêm chữ giải thích ngoài JSON
""";

        MimeType mimeType = toMimeType(image.getContentType());

        ByteArrayResource imageResource;
        try {
            imageResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return (image.getOriginalFilename() != null && !image.getOriginalFilename().isBlank())
                            ? image.getOriginalFilename()
                            : "body-image";
                }
            };
        } catch (Exception e) {
            // đọc bytes lỗi (hiếm) -> coi như external/file error
            throw new ExternalApiErrorException();
        }

        UserMessage userMessage = UserMessage.builder()
                .text(promptText)
                .media(new Media(mimeType, imageResource))
                .build();

        Prompt prompt = new Prompt(userMessage);

        Instant start = Instant.now();
        String raw;

        try {
            raw = chatModel.call(prompt)
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (HttpClientErrorException e) {
            // 4xx
            int status = e.getStatusCode().value();
            String msg = safePrefix(e.getResponseBodyAsString(), 300);

            if (status == 429) throw new AiQuotaExceededException();
            if (status == 403) throw new ForbiddenException("Không có quyền truy cập AI");
            if (status == 401) throw new UnauthorizedException("AI chưa xác thực");
            throw new ExternalApiErrorException("AI client error: " + status + " " + msg);

        } catch (HttpServerErrorException e) {
            // 5xx
            int status = e.getStatusCode().value();
            String msg = safePrefix(e.getResponseBodyAsString(), 300);
            throw new AiServiceUnavailableException("AI server error: " + status + " " + msg);

        } catch (ResourceAccessException e) {
            // thường là timeout/network
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new AiTimeoutException("AI timeout");
            }
            throw new ExternalApiTimeoutException("Kết nối tới AI thất bại hoặc quá chậm");

        } catch (Exception e) {
            // fallback: một số provider ném exception khác -> parse message
            String m = (e.getMessage() != null) ? e.getMessage() : "";
            String lower = m.toLowerCase();

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
        log.info("OpenRouter body analysis latency={}ms", latencyMs);

        // Lưu raw để debug dễ. Nếu lỗi lưu DB thì để DB layer xử lý (hoặc bạn wrap DB ở tầng khác)
        aiRepo.save(aiInteractionMapper.toEntity(
                user.getId(),
                assessment.getId(),
                AiTask.BODY_IMAGE_ANALYZE,
                promptText,
                raw,
                latencyMs,
                "openrouter"
        ));

        String json = extractJsonObject(raw);

        try {
            return objectMapper.readValue(json, BodyImageAiDto.class);
        } catch (Exception ex) {
            log.error("Failed to parse AI JSON. rawStart='{}' json='{}'",
                    safePrefix(raw, 120), safePrefix(json, 500));
            throw new AiResponseInvalidException();
        }
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
        if (start >= 0 && end > start) {
            t = t.substring(start, end + 1).trim();
        }

        if (!t.startsWith("{") || !t.endsWith("}")) return "{}";
        return t;
    }

    private String safePrefix(String s, int max) {
        if (s == null) return "";
        String t = s.replace("\n", "\\n").replace("\r", "\\r");
        return t.substring(0, Math.min(max, t.length()));
    }

    private MimeType toMimeType(String contentType) {
        if (contentType == null) return MimeTypeUtils.IMAGE_JPEG;
        String ct = contentType.toLowerCase();
        if (ct.contains("png")) return MimeTypeUtils.IMAGE_PNG;
        if (ct.contains("webp")) return MimeTypeUtils.parseMimeType("image/webp");
        return MimeTypeUtils.IMAGE_JPEG;
    }
}
