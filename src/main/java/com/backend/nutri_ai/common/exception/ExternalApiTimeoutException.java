package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ExternalApiTimeoutException extends BaseBusinessException {
    public ExternalApiTimeoutException() { super(ErrorCode.EXTERNAL_API_TIMEOUT); }
    public ExternalApiTimeoutException(String message) { super(ErrorCode.EXTERNAL_API_TIMEOUT, message); }
}
