package com.se.its.view.exception;

abstract public class IdException extends IllegalArgumentException {
    public IdException(String message) {
        super(message);
    }
}
