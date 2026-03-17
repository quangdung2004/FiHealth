package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class InvalidJsonException extends BaseBusinessException {
    public InvalidJsonException() { super(ErrorCode.INVALID_JSON); }
    public InvalidJsonException(String message) { super(ErrorCode.INVALID_JSON, message); }
}
