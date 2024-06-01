package com.se.its.domain.member.dto.request;

import com.se.its.domain.member.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberRoleUpdateRequestDto {
    private Long id;

    @NotNull
    private Role role;

}
