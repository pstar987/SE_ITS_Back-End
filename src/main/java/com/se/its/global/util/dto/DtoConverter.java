package com.se.its.global.util.dto;

import com.se.its.domain.comment.domain.Comment;
import com.se.its.domain.comment.domain.repository.CommentRepository;
import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.comment.dto.response.CommentResponseDto;
import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DtoConverter {
    private final ProjectMemberRepository projectMemberRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public MemberResponseDto createMemberResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }

    @Transactional(readOnly = true)
    public ProjectResponseDto createProjectResponseDto(Project project) {
        List<MemberResponseDto> memberResponseDtos = projectMemberRepository.findByProjectIdAndIsDeletedFalse(project.getId()).stream()
                .map(pm -> createMemberResponseDto(pm.getMember()))
                .toList();

        List<IssueResponseDto> issueIds = issueRepository.findByProjectIdAndIsDeletedFalse(project.getId()).stream()
                .map(this::createIssueResponseDto)
                .toList();

        return ProjectResponseDto.builder()
                .id(project.getId())
                .name(project.getName())
                .members(memberResponseDtos)
                .leaderId(project.getLeaderId())
                .issues(issueIds)
                .build();
    }

    @Transactional(readOnly = true)
    public IssueResponseDto createIssueResponseDto(Issue issue) {

        List<CommentResponseDto> comments = commentRepository.findByIssueIdAndIsDeletedFalse(issue.getId()).stream()
                .map(this::createCommentResponseDto)
                .toList();


        return IssueResponseDto.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .priority(issue.getPriority())
                .status(issue.getStatus())
                .reporter(issue.getReporter() != null ? createMemberResponseDto(issue.getReporter()) : null)
                .reportedDate(issue.getCreatedAt())
                .fixer(issue.getFixer() != null ? createMemberResponseDto(issue.getFixer()) : null)
                .assignee(issue.getAssignee() != null ? createMemberResponseDto(issue.getAssignee()) : null)
                .projectId(issue.getProject().getId())
                .comments(comments)
                .build();
    }

    @Transactional(readOnly = true)
    public CommentResponseDto createCommentResponseDto(Comment comment){
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writerId(comment.getWriter().getId())
                .build();
    }

    @Transactional(readOnly = true)
    public CommentCreateRequestDto createCommentRequestDto(Issue issue, String content){
        return CommentCreateRequestDto.builder()
                .issueId(issue.getId())
                .content(content)
                .build();
    }
}
