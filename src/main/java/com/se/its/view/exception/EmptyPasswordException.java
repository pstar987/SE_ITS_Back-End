package com.se.its.view.exception;

import com.se.its.view.util.ErrorMessage;

public class EmptyPasswordException extends PasswordException {
    public EmptyPasswordException() {
        super(ErrorMessage.EMPTY_PASSWORD.getMessage());
    }
}
