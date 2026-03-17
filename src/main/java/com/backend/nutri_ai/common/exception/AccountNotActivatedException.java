package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class AccountNotActivatedException extends BaseBusinessException {
    public AccountNotActivatedException() { super(ErrorCode.ACCOUNT_NOT_ACTIVATED); }
    public AccountNotActivatedException(String message) { super(ErrorCode.ACCOUNT_NOT_ACTIVATED, message); }
}
