package com.se.its.view.util;

public enum ErrorMessage {
    FAILED_TO_SIGNIN("로그인 실패"),
    EMPTY_DESCRIPTION("설명을 입력해주세요."),
    EMPTY_TITLE("제목을 입력해주세요."),
    WRONG_CONFIRM_PASSWORD("비밀번호가 일치하지 않습니다."),
    EMPTY_NAME("이름을 입력해주세요."),
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
