package com.se.its.domain.project.dto.response;

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
    private List<Long> issues;
    private Long leaderId;
}
