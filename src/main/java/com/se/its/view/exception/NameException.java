package com.se.its.view.exception;

abstract public class NameException extends IllegalArgumentException {
    public NameException(String message) {
        super(message);
    }
}
