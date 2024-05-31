package com.se.its.domain.project.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectMemberAddRequestDto {
    private Long addMemberId;
}
