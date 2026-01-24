package com.backend.nutri_ai.common.storage;

import com.backend.nutri_ai.common.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import java.util.UUID;

@Service
@Slf4j
public class LocalStorageService {

    @Value("${app.storage.local-dir}")
    private String rootDir;

    public StoredFile save(UUID assessmentId, MultipartFile file) {
        try {
            Files.createDirectories(Path.of(rootDir));

            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";
            String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
            String filename = assessmentId + "_" + System.currentTimeMillis() + "_" + safeName;

            Path target = Path.of(rootDir, filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            log.info("Saved image file: {}", target);

            return StoredFile.builder()
                    .path(target.toString())
                    .originalFilename(original)
                    .contentType(file.getContentType())
                    .sizeBytes(file.getSize())
                    .build();

        } catch (IOException e) {
            log.error("Failed to save image", e);
            throw new StorageException("Cannot save image file", e);
        }
    }
}
