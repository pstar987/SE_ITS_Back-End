package com.se.its.view.exception;

abstract public class PasswordException extends IllegalArgumentException {
    public PasswordException(String message) {
        super(message);
    }
}
