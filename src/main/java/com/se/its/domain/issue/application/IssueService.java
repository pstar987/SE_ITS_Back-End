package com.se.its.domain.issue.application;


import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.domain.Status;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import com.se.its.domain.issue.dto.request.IssueAssignRequestDto;
import com.se.its.domain.issue.dto.request.IssueCreateRequestDto;
import com.se.its.domain.issue.dto.request.IssueDeleteRequestDto;
import com.se.its.domain.issue.dto.request.IssueUpdateRequestDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.global.error.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.se.its.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public IssueResponseDto createIssue(Long signId, IssueCreateRequestDto issueCreateRequestDto){
        Member reporter = getUser(signId);
        Project project = getProject(issueCreateRequestDto.getProjectId());

        if(!reporter.getRole().equals(Role.TESTER)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "TESTER가 아닙니다.");
        }
        isMemberOfProject(reporter, project);

        Issue issue = Issue.builder()
                .title(issueCreateRequestDto.getTitle())
                .description(issueCreateRequestDto.getDescription())
                .reporter(reporter)
                .project(project)
                .isDeleted(false)
                .priority(Priority.MINOR) // 기본 값 설정
                .status(Status.NEW) // 기본 값 설정
                .build();

        Issue savedIssue = issueRepository.save(issue);

        return createIssueResponseDto(savedIssue);

    }

    @Transactional(readOnly = true)
    public List<IssueResponseDto> getIssues(Long signId, Long projectId) {
        Member member = getUser(signId);
        Project project = getProject(projectId);

        isMemberOfProject(member, project);


        if (member.getRole().equals(Role.ADMIN)) {
            // admin은 모든 프로젝트의 모든 이슈 조회 가능
            return issueRepository.findByProjectIdAndIsDeletedFalse(project.getId()).stream()
                    .map(this::createIssueResponseDto).toList();
        } else if (member.getRole().equals(Role.PL) || member.getRole().equals(Role.TESTER)) {
            return issueRepository.findByProjectIdAndIsDeletedFalse(projectId).stream()
                    .map(this::createIssueResponseDto).toList();
        } else if(member.getRole().equals(Role.DEV)){
            return issueRepository.findByProjectIdAndAssigneeIdAndIsDeletedFalse(project.getId(), member.getId()).stream()
                    .map(this::createIssueResponseDto).toList();
        } else {
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "권한이 없는 사용자입니다.");
        }
    }



    @Transactional(readOnly = true)
    public List<IssueResponseDto> getAllIssues(Long signId) {
        Member admin = getUser(signId);
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "관리자만 모든 이슈를 조회할 수 있습니다.");
        }
        return issueRepository.findAllByIsDeletedFalse().stream()
                .map(this::createIssueResponseDto).toList();
    }


    @Transactional
    public IssueResponseDto assignIssue(Long signId, IssueAssignRequestDto issueAssignRequestDto) {
        Member plMember = getUser(signId);
        Issue issue = getIssue(issueAssignRequestDto.getIssueId());
        Project project = issue.getProject();
        Member assignee = getUser(issueAssignRequestDto.getAssigneeId());

        isMemberOfProject(plMember, project);
        isMemberOfProject(assignee, project);

        if (!plMember.getRole().equals(Role.PL)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "프로젝트 리더만 이슈를 할당할 수 있습니다.");
        }
        if (!assignee.getRole().equals(Role.DEV)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "할당할 멤버는 DEV 역할이어야 합니다.");
        }
        if (!issue.getStatus().equals(Status.NEW)) {
            throw new BadRequestException(INVALID_REQUEST_STATUS, "이슈는 NEW 상태일 때만 할당할 수 있습니다.");
        }


        issue.setAssignee(assignee);
        issue.setStatus(Status.ASSIGNED); // 상태를 ASSIGNED로 변경
        issueRepository.save(issue);
        return createIssueResponseDto(issue);
    }

    @Transactional
    public IssueResponseDto removeRequest(Long signId, IssueDeleteRequestDto issueDeleteRequestDto) {
        Member tester = getUser(signId);
        Issue issue = getIssue(issueDeleteRequestDto.getIssueId());

        if (!tester.getRole().equals(Role.TESTER)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "TESTER만 이슈 삭제를 요청할 수 있습니다.");
        }
        if (!issue.getReporter().getId().equals(tester.getId())) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "본인이 생성한 이슈만 삭제 요청할 수 있습니다.");
        }

        issue.setStatus(Status.DELETE_REQUEST);
        issueRepository.save(issue);
        return createIssueResponseDto(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponseDto> getRemoveRequestIssues(Long signId) {
        Member admin = getUser(signId);

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자만 삭제 요청 이슈를 조회할 수 있습니다.");
        }
        return issueRepository.findByStatusAndIsDeletedFalse(Status.DELETE_REQUEST).stream()
                .map(this::createIssueResponseDto)
                .toList();
    }

    @Transactional
    public IssueResponseDto removeIssue(Long signId, IssueDeleteRequestDto issueDeleteRequestDto){
        Member admin = getUser(signId);
        Issue issue = getIssue(issueDeleteRequestDto.getIssueId());

        if(!admin.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자만 이슈를 삭제할 수 있습니다.");
        }
        if (!issue.getStatus().equals(Status.DELETE_REQUEST)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "삭제 요청된 이슈만 삭제할 수 있습니다.");
        }

        issue.setIsDeleted(true);
        issueRepository.save(issue);
        return createIssueResponseDto(issue);

    }

    @Transactional
    public IssueResponseDto updateIssue(Long signId, IssueUpdateRequestDto issueUpdateRequestDto){
        Member tester = getUser(signId);
        Issue issue = getIssue(issueUpdateRequestDto.getIssueId());


        if (!issue.getReporter().getId().equals(tester.getId())) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "본인이 생성한 이슈만 수정할 수 있습니다.");
        }
        if (!tester.getRole().equals(Role.TESTER)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "TESTER만 이슈를 수정할 수 있습니다.");
        }

        issue.setDescription(issueUpdateRequestDto.getDescription());
        issueRepository.save(issue);
        return createIssueResponseDto(issue);
    }


    private void isMemberOfProject(Member member, Project project) {
        if(!member.getRole().equals(Role.ADMIN)){
            projectMemberRepository.findByMemberIdAndProjectIdAndIsDeletedFalse(member.getId(), project.getId())
                    .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "해당 프로젝트의 멤버가 아닙니다."));
        }
    }

    private IssueResponseDto createIssueResponseDto(Issue issue) {
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
                .isDeleted(issue.getIsDeleted())
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


    private Issue getIssue(Long issueId) {
        return issueRepository.findByIdAndIsDeletedFalse(issueId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 이슈입니다."));
    }

}
