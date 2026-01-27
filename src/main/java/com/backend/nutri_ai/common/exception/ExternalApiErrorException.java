package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ExternalApiErrorException extends BaseBusinessException {
    public ExternalApiErrorException() { super(ErrorCode.EXTERNAL_API_ERROR); }
    public ExternalApiErrorException(String message) { super(ErrorCode.EXTERNAL_API_ERROR, message); }
}