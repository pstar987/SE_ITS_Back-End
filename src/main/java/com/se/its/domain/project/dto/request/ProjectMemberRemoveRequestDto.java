package com.se.its.domain.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberRemoveRequestDto {
    private Long removeMemberId;
}
