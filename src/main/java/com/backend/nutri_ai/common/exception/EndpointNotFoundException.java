package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class EndpointNotFoundException extends BaseBusinessException {
    public EndpointNotFoundException() { super(ErrorCode.ENDPOINT_NOT_FOUND); }
    public EndpointNotFoundException(String message) { super(ErrorCode.ENDPOINT_NOT_FOUND, message); }
}
