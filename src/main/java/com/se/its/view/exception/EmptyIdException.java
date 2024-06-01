package com.se.its.view.exception;

import com.se.its.view.util.ErrorMessage;

public class EmptyIdException extends IdException {
    public EmptyIdException() {
        super(ErrorMessage.EMPTY_ID.getMessage());
    }
}
