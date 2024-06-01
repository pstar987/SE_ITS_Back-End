package com.se.its.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSignInRequestDto {

    @NotBlank(message = "id를 입력해주세요")
    private String signId;

    @NotBlank(message = "PW는 공백이면 안 됩니다.")
    private String password;

}
