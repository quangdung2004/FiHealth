package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class UserBlockedException extends BaseBusinessException {
    public UserBlockedException() { super(ErrorCode.USER_BLOCKED); }
    public UserBlockedException(String message) { super(ErrorCode.USER_BLOCKED, message); }
}
