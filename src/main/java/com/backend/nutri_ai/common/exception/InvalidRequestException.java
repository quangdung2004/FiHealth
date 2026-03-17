package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class InvalidRequestException extends BaseBusinessException {
    public InvalidRequestException() { super(ErrorCode.INVALID_REQUEST); }
    public InvalidRequestException(String message) { super(ErrorCode.INVALID_REQUEST, message); }
}
