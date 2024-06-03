package com.se.its.domain.member.dto.response;

import com.se.its.domain.member.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {

    private Long id;

    private Role role;

    private String name;

    private String signId;

    private Boolean isDeleted;

}
