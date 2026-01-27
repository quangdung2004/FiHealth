package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ExpiredRefreshTokenException extends BaseBusinessException {
    public ExpiredRefreshTokenException() { super(ErrorCode.EXPIRED_REFRESH_TOKEN); }
    public ExpiredRefreshTokenException(String message) { super(ErrorCode.EXPIRED_REFRESH_TOKEN, message); }
}
