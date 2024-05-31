package com.se.its.domain.member.dto.request;

import com.se.its.domain.member.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MemberRoleUpdateRequestDto {
    @NotBlank(message = "올바른 사용자 id를 입력해주세요.")
    private String signId;

    @NotNull
    private Role role;

}
