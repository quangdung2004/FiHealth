package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class AiResponseInvalidException extends BaseBusinessException {
    public AiResponseInvalidException() { super(ErrorCode.AI_RESPONSE_INVALID); }
    public AiResponseInvalidException(String message) { super(ErrorCode.AI_RESPONSE_INVALID, message); }
}
