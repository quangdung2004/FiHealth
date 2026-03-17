package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class MissingRequiredFieldException extends BaseBusinessException {
    public MissingRequiredFieldException() { super(ErrorCode.MISSING_REQUIRED_FIELD); }
    public MissingRequiredFieldException(String message) { super(ErrorCode.MISSING_REQUIRED_FIELD, message); }
}
