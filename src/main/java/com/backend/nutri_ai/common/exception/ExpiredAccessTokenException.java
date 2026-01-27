package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ExpiredAccessTokenException extends BaseBusinessException {
    public ExpiredAccessTokenException() { super(ErrorCode.EXPIRED_ACCESS_TOKEN); }
    public ExpiredAccessTokenException(String message) { super(ErrorCode.EXPIRED_ACCESS_TOKEN, message); }
}
