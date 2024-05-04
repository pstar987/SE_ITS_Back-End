package com.se.its.global.error.exceptions;

import com.se.its.global.error.BaseRuntimeException;
import com.se.its.global.error.ErrorCode;

public class UnauthorizedException extends BaseRuntimeException {
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
