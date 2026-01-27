package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class UploadTooLargeException extends BaseBusinessException {
    public UploadTooLargeException() { super(ErrorCode.UPLOAD_TOO_LARGE); }
    public UploadTooLargeException(String message) { super(ErrorCode.UPLOAD_TOO_LARGE, message); }
}
