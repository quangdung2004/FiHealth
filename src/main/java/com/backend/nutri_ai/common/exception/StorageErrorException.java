package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class StorageErrorException extends BaseBusinessException {
    public StorageErrorException() { super(ErrorCode.STORAGE_ERROR); }
    public StorageErrorException(String message) { super(ErrorCode.STORAGE_ERROR, message); }
}