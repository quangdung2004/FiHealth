package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ConstraintViolationException extends BaseBusinessException {
    public ConstraintViolationException() { super(ErrorCode.CONSTRAINT_VIOLATION); }
    public ConstraintViolationException(String message) { super(ErrorCode.CONSTRAINT_VIOLATION, message); }
}
