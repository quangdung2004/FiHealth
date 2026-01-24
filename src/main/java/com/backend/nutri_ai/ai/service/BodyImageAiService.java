package com.backend.nutri_ai.ai.service;

import com.backend.nutri_ai.ai.dto.BodyImageAiDto;
import com.backend.nutri_ai.ai.mapper.AiInteractionMapper;
import com.backend.nutri_ai.ai.repository.AiInteractionRepository;
import com.backend.nutri_ai.assessment.entity.NutritionAssessment;
import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.enums.AiTask;
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
import org.springframework.web.multipart.MultipartFile;

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

    public BodyImageAiDto analyze(AppUser user, NutritionAssessment assessment, MultipartFile image) throws Exception {

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

        ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return (image.getOriginalFilename() != null && !image.getOriginalFilename().isBlank())
                        ? image.getOriginalFilename()
                        : "body-image";
            }
        };

        UserMessage userMessage = UserMessage.builder()
                .text(promptText)
                .media(new Media(mimeType, imageResource))
                .build();

        Prompt prompt = new Prompt(userMessage);

        Instant start = Instant.now();

        String raw = chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();

        int latencyMs = (int) Duration.between(start, Instant.now()).toMillis();
        log.info("OpenRouter body analysis latency={}ms", latencyMs);

        // Lưu raw để debug dễ (đừng sanitize khi lưu)
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
            // log thêm để nhìn nhanh raw/json thực tế
            log.error("Failed to parse AI JSON. rawStart='{}' json='{}'",
                    safePrefix(raw, 120), safePrefix(json, 500));
            throw ex;
        }
    }

    /**
     * Bỏ ```json ... ``` và cố gắng cắt lấy JSON object {...} đầu tiên.
     * Mục tiêu: parse được kể cả khi AI trả kèm markdown/text.
     */
    private String extractJsonObject(String s) {
        if (s == null) return "{}";
        String t = s.trim();

        // 1) Remove code fences ```...```
        if (t.startsWith("```")) {
            // remove first fence line ``` or ```json
            int firstNewline = t.indexOf('\n');
            if (firstNewline > 0) t = t.substring(firstNewline + 1);
            // remove trailing ```
            int lastFence = t.lastIndexOf("```");
            if (lastFence >= 0) t = t.substring(0, lastFence);
            t = t.trim();
        }

        // 2) Find first '{' and last '}' to get a JSON object
        int start = t.indexOf('{');
        int end = t.lastIndexOf('}');
        if (start >= 0 && end > start) {
            t = t.substring(start, end + 1).trim();
        }

        // 3) Nếu vẫn không phải object (kịch bản xấu), trả về tối thiểu JSON hợp lệ
        if (!t.startsWith("{") || !t.endsWith("}")) {
            return "{}";
        }
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
