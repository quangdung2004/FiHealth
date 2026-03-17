package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class InvalidOtpException extends BaseBusinessException {

    public InvalidOtpException() {
        super(ErrorCode.INVALID_OTP);
    }

    public InvalidOtpException(String message) {
        super(ErrorCode.INVALID_OTP, message);
    }
}
