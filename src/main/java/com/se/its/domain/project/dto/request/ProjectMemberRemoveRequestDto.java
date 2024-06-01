package com.se.its.domain.project.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProjectMemberRemoveRequestDto {
    private Long removeMemberId;
}
