package com.backend.nutri_ai.common.storage;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoredFile {
    private String path;
    private String originalFilename;
    private String contentType;
    private long sizeBytes;
}
