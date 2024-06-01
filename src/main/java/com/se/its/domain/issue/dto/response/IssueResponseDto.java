package com.se.its.domain.issue.dto.response;

import com.se.its.domain.comment.domain.Comment;
import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.domain.Status;
import com.se.its.domain.member.dto.response.MemberResponseDto;
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
    private LocalDateTime reporterDate;
    private MemberResponseDto fixer;
    private MemberResponseDto assignee;
//    private List<> comments;
}
