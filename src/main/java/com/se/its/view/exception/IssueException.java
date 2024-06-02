package com.se.its.view.exception;

abstract public class IssueException extends IllegalArgumentException {
    public IssueException(String message) {
        super(message);
    }
}
