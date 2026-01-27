package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class DatabaseConnectionFailedException extends BaseBusinessException {
    public DatabaseConnectionFailedException() { super(ErrorCode.DATABASE_CONNECTION_FAILED); }
    public DatabaseConnectionFailedException(String message) { super(ErrorCode.DATABASE_CONNECTION_FAILED, message); }
}
