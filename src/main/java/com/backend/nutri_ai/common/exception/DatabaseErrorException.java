package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class DatabaseErrorException extends BaseBusinessException {
    public DatabaseErrorException() { super(ErrorCode.DATABASE_ERROR); }
    public DatabaseErrorException(String message) { super(ErrorCode.DATABASE_ERROR, message); }
}
