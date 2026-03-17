package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ServiceUnavailableException extends BaseBusinessException {
    public ServiceUnavailableException() { super(ErrorCode.SERVICE_UNAVAILABLE); }
    public ServiceUnavailableException(String message) { super(ErrorCode.SERVICE_UNAVAILABLE, message); }
}
