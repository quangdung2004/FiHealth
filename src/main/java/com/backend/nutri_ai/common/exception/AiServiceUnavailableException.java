package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class AiServiceUnavailableException extends BaseBusinessException {
    public AiServiceUnavailableException() { super(ErrorCode.AI_SERVICE_UNAVAILABLE); }
    public AiServiceUnavailableException(String message) { super(ErrorCode.AI_SERVICE_UNAVAILABLE, message); }
}
