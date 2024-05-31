package com.se.its.domain.member.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class MemberUpdateRequestDto {

    private Long id;

    @NotBlank(message = "PW는 공백이면 안 됩니다.")
    private String password;

    @NotBlank(message = "PW는 공백이면 안 됩니다.")
    private String passwordCheck;



}
