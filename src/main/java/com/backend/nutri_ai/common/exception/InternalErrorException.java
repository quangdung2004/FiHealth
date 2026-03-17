package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class InternalErrorException extends BaseBusinessException {
    public InternalErrorException() { super(ErrorCode.INTERNAL_ERROR); }
    public InternalErrorException(String message) { super(ErrorCode.INTERNAL_ERROR, message); }
}
