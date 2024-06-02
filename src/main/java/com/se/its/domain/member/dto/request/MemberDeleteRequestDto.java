package com.se.its.domain.member.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter

public class MemberDeleteRequestDto {
    private Long id;
}
