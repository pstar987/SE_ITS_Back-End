package com.se.its.domain.issue.application;


import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.domain.Status;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import com.se.its.domain.issue.dto.request.IssueCreateRequestDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.global.error.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.se.its.global.error.ErrorCode.INVALID_REQUEST_ROLE;
import static com.se.its.global.error.ErrorCode.ROW_DOES_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;

    public IssueResponseDto createIssue(Long signId, IssueCreateRequestDto issueCreateRequestDto){
        Member reporter = getUser(signId);
        Project project = getProject(issueCreateRequestDto.getProjectId());

        if(!reporter.getRole().equals(Role.TESTER)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "TESTER가 아닙니다.");
        }

        Issue issue = Issue.builder()
                .title(issueCreateRequestDto.getTitle())
                .description(issueCreateRequestDto.getDescription())
                .reporter(reporter)
                .project(project)
                .priority(Priority.MINOR) // 기본 값 설정
                .status(Status.NEW) // 기본 값 설정
                .build();

        Issue savedIssue = issueRepository.save(issue);

        return createIssueResponseDto(savedIssue);

    }

    private IssueResponseDto createIssueResponseDto(Issue issue) {
        return IssueResponseDto.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .priority(issue.getPriority())
                .status(issue.getStatus())
                .reporter(createMemberResponseDto(issue.getReporter()))
                .reporterDate(issue.getCreatedAt())
                .fixer(issue.getFixer() != null ? createMemberResponseDto(issue.getFixer()) : null)
                .assignee(issue.getAssignee() != null ? createMemberResponseDto(issue.getAssignee()) : null)
                .build();
    }

    private MemberResponseDto createMemberResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .role(member.getRole())
                .isDeleted(member.getIsDeleted())
                .build();
    }



    private Member getUser(Long targetId){
        return memberRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 사용자입니다."));
    }

    private Project getProject(Long projectId){
        return projectRepository.findByIdAndIsDeletedIsFalse(projectId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "프로젝트가 존재하지 않습니다."));
    }


}
