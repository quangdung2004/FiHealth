package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class AiQuotaExceededException extends BaseBusinessException {
    public AiQuotaExceededException() { super(ErrorCode.AI_QUOTA_EXCEEDED); }
    public AiQuotaExceededException(String message) { super(ErrorCode.AI_QUOTA_EXCEEDED, message); }
}
