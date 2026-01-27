package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class DatabaseTimeoutException extends BaseBusinessException {
    public DatabaseTimeoutException() { super(ErrorCode.DATABASE_TIMEOUT); }
    public DatabaseTimeoutException(String message) { super(ErrorCode.DATABASE_TIMEOUT, message); }
}
