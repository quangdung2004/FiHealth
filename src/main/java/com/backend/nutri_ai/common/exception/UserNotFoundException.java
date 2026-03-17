package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class UserNotFoundException extends BaseBusinessException {
    public UserNotFoundException() { super(ErrorCode.USER_NOT_FOUND); }
    public UserNotFoundException(String message) { super(ErrorCode.USER_NOT_FOUND, message); }
}
