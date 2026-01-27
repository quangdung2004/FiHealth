package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class EmailAlreadyExistsException extends BaseBusinessException {
    public EmailAlreadyExistsException() { super(ErrorCode.EMAIL_ALREADY_EXISTS); }
    public EmailAlreadyExistsException(String message) { super(ErrorCode.EMAIL_ALREADY_EXISTS, message); }
}