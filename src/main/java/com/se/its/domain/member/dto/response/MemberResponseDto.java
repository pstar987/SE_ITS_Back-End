package com.se.its.domain.member.dto.response;

import com.se.its.domain.member.domain.Role;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class MemberResponseDto {

    private Long id;

    private Role role;

    private Boolean isDeleted;

    private String name;

}
