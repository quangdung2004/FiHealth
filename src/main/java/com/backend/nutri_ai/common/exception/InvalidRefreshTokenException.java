package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class InvalidRefreshTokenException extends BaseBusinessException {
    public InvalidRefreshTokenException() { super(ErrorCode.INVALID_REFRESH_TOKEN); }
    public InvalidRefreshTokenException(String message) { super(ErrorCode.INVALID_REFRESH_TOKEN, message); }
}
