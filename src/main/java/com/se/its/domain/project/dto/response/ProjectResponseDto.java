package com.se.its.domain.project.dto.response;

import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProjectResponseDto {
    private Long id;
    private String name;
    private List<MemberResponseDto> members;
    private List<IssueResponseDto> issues;
    private Long leaderId;
    private Boolean isDeleted;
}
