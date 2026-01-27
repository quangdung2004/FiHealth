package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class FileNotFoundException extends BaseBusinessException {
    public FileNotFoundException() { super(ErrorCode.FILE_NOT_FOUND); }
    public FileNotFoundException(String message) { super(ErrorCode.FILE_NOT_FOUND, message); }
}
