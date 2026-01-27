package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class InvalidCredentialsException extends BaseBusinessException {
    public InvalidCredentialsException() { super(ErrorCode.INVALID_CREDENTIALS); }
    public InvalidCredentialsException(String message) { super(ErrorCode.INVALID_CREDENTIALS, message); }
}
