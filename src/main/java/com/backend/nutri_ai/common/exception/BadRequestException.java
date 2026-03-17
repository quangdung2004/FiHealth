package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class BadRequestException extends BaseBusinessException {
    public BadRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
    public BadRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }
}
