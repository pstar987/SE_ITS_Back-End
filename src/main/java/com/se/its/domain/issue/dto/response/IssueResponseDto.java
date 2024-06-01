package com.se.its.domain.issue.dto.response;

import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.domain.Status;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class IssueResponseDto {
    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private MemberResponseDto reporter;
    private LocalDateTime reportedDate;
    private MemberResponseDto fixer;
    private MemberResponseDto assignee;
    private Long projectId;
//    private List<> comments;
}
