package com.se.its.view.exception;

import com.se.its.view.util.ErrorMessage;

public class EmptyIssueTitleException extends IssueException {
    public EmptyIssueTitleException() {
        super(ErrorMessage.EMPTY_TITLE.getMessage());
    }
}
