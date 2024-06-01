package com.se.its.domain.member.dto.request;

import com.se.its.domain.member.domain.Role;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class MemberSignUpRequestDto {

    @NotBlank(message = "id를 입력해주세요.")
    private String signId;

    @NotBlank(message = "PW는 공백이면 안 됩니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotNull
    private Role role;

}
