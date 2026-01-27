package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class AiTimeoutException extends BaseBusinessException {
    public AiTimeoutException() { super(ErrorCode.AI_TIMEOUT); }
    public AiTimeoutException(String message) { super(ErrorCode.AI_TIMEOUT, message); }
}
