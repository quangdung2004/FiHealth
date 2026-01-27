package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ExpiredOtpException extends BaseBusinessException {

    public ExpiredOtpException() {
        super(ErrorCode.EXPIRED_OTP);
    }

    public ExpiredOtpException(String message) {
        super(ErrorCode.EXPIRED_OTP, message);
    }
}
