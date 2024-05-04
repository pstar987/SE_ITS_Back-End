package com.se.its.global.error.exceptions;

import com.se.its.global.error.BaseRuntimeException;
import com.se.its.global.error.ErrorCode;

public class InternalServerException extends BaseRuntimeException {
    public InternalServerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
