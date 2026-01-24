package com.backend.nutri_ai.common.exception;

import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {

    private final String code;

    public StorageException(String message, Throwable cause) {
        super(message, cause);
        this.code = "STORAGE_ERROR";
    }

    public StorageException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}

