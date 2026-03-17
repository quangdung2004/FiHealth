package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class DataIntegrityViolationBusinessException extends BaseBusinessException {
    public DataIntegrityViolationBusinessException() { super(ErrorCode.DATA_INTEGRITY_VIOLATION); }
    public DataIntegrityViolationBusinessException(String message) { super(ErrorCode.DATA_INTEGRITY_VIOLATION, message); }
}
