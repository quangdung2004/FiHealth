package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ResourceNotFoundException extends BaseBusinessException {
    public ResourceNotFoundException() { super(ErrorCode.RESOURCE_NOT_FOUND); }
    public ResourceNotFoundException(String message) { super(ErrorCode.RESOURCE_NOT_FOUND, message); }
}
