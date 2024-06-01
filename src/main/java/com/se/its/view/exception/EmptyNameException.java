package com.se.its.view.exception;

import com.se.its.view.util.ErrorMessage;

public class EmptyNameException extends NameException {
    public EmptyNameException() {
        super(ErrorMessage.EMPTY_NAME.getMessage());
    }
}
