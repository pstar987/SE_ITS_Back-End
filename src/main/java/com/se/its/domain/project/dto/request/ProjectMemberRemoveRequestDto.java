package com.se.its.domain.project.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectMemberRemoveRequestDto {
    private Long removeMemberId;
}
