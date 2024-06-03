package com.se.its.view.exception;

import com.se.its.view.util.ErrorMessage;

public class EmptyIssueDescriptionException extends IssueException {
    public EmptyIssueDescriptionException() {
        super(ErrorMessage.EMPTY_DESCRIPTION.getMessage());
    }
}
