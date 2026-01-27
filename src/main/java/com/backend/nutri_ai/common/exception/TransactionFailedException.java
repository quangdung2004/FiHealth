package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class TransactionFailedException extends BaseBusinessException {
    public TransactionFailedException() { super(ErrorCode.TRANSACTION_FAILED); }
    public TransactionFailedException(String message) { super(ErrorCode.TRANSACTION_FAILED, message); }
}
