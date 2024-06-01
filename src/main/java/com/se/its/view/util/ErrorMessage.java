package com.se.its.view.util;

public enum ErrorMessage {
    FAILED_TO_SIGNIN("로그인 실패"),
    EMPTY_ID("아이디를 입력해주세요."),
    EMPTY_PASSWORD("비밀번호를 입력해주세요.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
