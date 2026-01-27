package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class UnauthorizedException extends BaseBusinessException {
    public UnauthorizedException() { super(ErrorCode.UNAUTHORIZED); }
    public UnauthorizedException(String message) { super(ErrorCode.UNAUTHORIZED, message); }
}
