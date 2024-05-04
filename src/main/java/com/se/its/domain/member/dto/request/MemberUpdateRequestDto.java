package com.se.its.domain.member.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class MemberUpdateRequestDto {

    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "PW는 공백이면 안 됩니다.")
    private String password;

}
