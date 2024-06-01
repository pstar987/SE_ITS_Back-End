package com.se.its.global.util.dto;

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

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DtoConverter {
    private final ProjectMemberRepository projectMemberRepository;
    private final IssueRepository issueRepository;

    public MemberResponseDto createMemberResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .role(member.getRole())
                .isDeleted(member.getIsDeleted())
                .build();
    }

    public ProjectResponseDto createProjectResponseDto(Project project) {
        List<MemberResponseDto> memberResponseDtos = projectMemberRepository.findByProjectIdAndIsDeletedFalse(project.getId()).stream()
                .map(pm -> createMemberResponseDto(pm.getMember()))
                .collect(Collectors.toList());

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

    public IssueResponseDto createIssueResponseDto(Issue issue) {
        return IssueResponseDto.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .priority(issue.getPriority())
                .status(issue.getStatus())
                .reporter(createMemberResponseDto(issue.getReporter()))
                .reportedDate(issue.getCreatedAt())
                .fixer(issue.getFixer() != null ? createMemberResponseDto(issue.getFixer()) : null)
                .assignee(issue.getAssignee() != null ? createMemberResponseDto(issue.getAssignee()) : null)
                .projectId(issue.getProject().getId())
                .build();
    }
}
