package com.se.its.global.error.exceptions;

import com.se.its.global.error.BaseRuntimeException;
import com.se.its.global.error.ErrorCode;

public class BadRequestException extends BaseRuntimeException {
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
