package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class InvalidAccessTokenException extends BaseBusinessException {
    public InvalidAccessTokenException() { super(ErrorCode.INVALID_ACCESS_TOKEN); }
    public InvalidAccessTokenException(String message) { super(ErrorCode.INVALID_ACCESS_TOKEN, message); }
}
