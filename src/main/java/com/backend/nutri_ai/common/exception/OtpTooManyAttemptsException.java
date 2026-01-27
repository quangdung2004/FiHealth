package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class OtpTooManyAttemptsException extends BaseBusinessException {
    public OtpTooManyAttemptsException() { super(ErrorCode.OTP_TOO_MANY_ATTEMPTS); }
    public OtpTooManyAttemptsException(String message) { super(ErrorCode.OTP_TOO_MANY_ATTEMPTS, message); }
}
