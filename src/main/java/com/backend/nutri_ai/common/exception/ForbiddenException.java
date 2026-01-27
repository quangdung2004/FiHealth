package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ForbiddenException extends BaseBusinessException {
    public ForbiddenException() { super(ErrorCode.FORBIDDEN); }
    public ForbiddenException(String message) { super(ErrorCode.FORBIDDEN, message); }
}
