package com.se.its.view.exception;

import com.se.its.view.util.ErrorMessage;

public class ConfirmPasswordException extends PasswordException {
    public ConfirmPasswordException() {
        super(ErrorMessage.WRONG_CONFIRM_PASSWORD.getMessage());
    }
}
