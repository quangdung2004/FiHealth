package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class DuplicatedResourceException extends BaseBusinessException {
    public DuplicatedResourceException() { super(ErrorCode.DUPLICATED_RESOURCE); }
    public DuplicatedResourceException(String message) { super(ErrorCode.DUPLICATED_RESOURCE, message); }
}
