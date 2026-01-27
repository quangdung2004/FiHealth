package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class TooManyRequestsException extends BaseBusinessException {
    public TooManyRequestsException() { super(ErrorCode.TOO_MANY_REQUESTS); }
    public TooManyRequestsException(String message) { super(ErrorCode.TOO_MANY_REQUESTS, message); }
}
