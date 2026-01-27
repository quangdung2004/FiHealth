package com.backend.nutri_ai.common.exception;

import com.backend.nutri_ai.common.enums.ErrorCode;

public class ImageRequiredException extends BaseBusinessException {
    public ImageRequiredException() { super(ErrorCode.IMAGE_REQUIRED); }
    public ImageRequiredException(String message) { super(ErrorCode.IMAGE_REQUIRED, message); }
}

