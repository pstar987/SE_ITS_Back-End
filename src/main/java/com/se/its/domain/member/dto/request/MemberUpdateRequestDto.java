package com.se.its.domain.member.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class MemberUpdateRequestDto {

    @NotBlank(message = "올바른 id를 입력해주세요.")
    private String signId;

    @NotBlank(message = "PW는 공백이면 안 됩니다.")
    private String password;

    @NotBlank(message = "PW는 공백이면 안 됩니다.")
    private String passwordCheck;



}
