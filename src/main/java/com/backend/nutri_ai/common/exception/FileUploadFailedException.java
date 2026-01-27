package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class FileUploadFailedException extends BaseBusinessException {
    public FileUploadFailedException() { super(ErrorCode.FILE_UPLOAD_FAILED); }
    public FileUploadFailedException(String message) { super(ErrorCode.FILE_UPLOAD_FAILED, message); }
}
