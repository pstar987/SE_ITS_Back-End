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
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.global.error.exceptions.BadRequestException;
import com.se.its.global.util.dto.DtoConverter;
import com.se.its.global.util.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.se.its.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final DtoConverter dtoConverter;
    private final EntityValidator entityValidator;

    @Transactional
    public IssueResponseDto createIssue(Long signId, IssueCreateRequestDto issueCreateRequestDto){
        Member reporter = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(issueCreateRequestDto.getProjectId());

        if(!reporter.getRole().equals(Role.TESTER)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "TESTER가 아닙니다.");
        }
        entityValidator.isMemberOfProject(reporter, project);

        Issue issue = Issue.builder()
                .title(issueCreateRequestDto.getTitle())
                .description(issueCreateRequestDto.getDescription())
                .reporter(reporter)
                .project(project)
                .isDeleted(false)
                .priority(Priority.MINOR) // 기본 값 설정
                .status(Status.NEW) // 기본 값 설정
                .category(issueCreateRequestDto.getCategory())
                .build();

        Issue savedIssue = issueRepository.save(issue);

        return dtoConverter.createIssueResponseDto(savedIssue);

    }

    @Transactional(readOnly = true)
    public List<IssueResponseDto> getIssues(Long signId, Long projectId) {
        Member member = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);

        entityValidator.isMemberOfProject(member, project);


        if (member.getRole().equals(Role.ADMIN)) {
            // admin은 모든 프로젝트의 모든 이슈 조회 가능
            return issueRepository.findByProjectIdAndIsDeletedFalse(project.getId()).stream()
                    .map(dtoConverter::createIssueResponseDto).toList();
        } else if (member.getRole().equals(Role.PL) || member.getRole().equals(Role.TESTER)) {
            return issueRepository.findByProjectIdAndIsDeletedFalse(projectId).stream()
                    .map(dtoConverter::createIssueResponseDto).toList();
        } else if(member.getRole().equals(Role.DEV)){
            return issueRepository.findByProjectIdAndAssigneeIdAndIsDeletedFalse(project.getId(), member.getId()).stream()
                    .map(dtoConverter::createIssueResponseDto).toList();
        } else {
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "권한이 없는 사용자입니다.");
        }
    }



    @Transactional(readOnly = true)
    public List<IssueResponseDto> getAllIssues(Long signId) {
        Member admin = entityValidator.validateMember(signId);
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "관리자만 모든 이슈를 조회할 수 있습니다.");
        }
        return issueRepository.findAllByIsDeletedFalse().stream()
                .map(dtoConverter::createIssueResponseDto).toList();
    }


    @Transactional
    public IssueResponseDto assignIssue(Long signId, IssueAssignRequestDto issueAssignRequestDto) {
        Member plMember = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueAssignRequestDto.getIssueId());
        Project project = issue.getProject();
        Member assignee = entityValidator.validateMember(issueAssignRequestDto.getAssigneeId());

        entityValidator.isMemberOfProject(plMember, project);
        entityValidator.isMemberOfProject(assignee, project);

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
        return dtoConverter.createIssueResponseDto(issue);
    }

    @Transactional
    public IssueResponseDto removeRequest(Long signId, IssueDeleteRequestDto issueDeleteRequestDto) {
        Member tester = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueDeleteRequestDto.getIssueId());

        if (!tester.getRole().equals(Role.TESTER)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "TESTER만 이슈 삭제를 요청할 수 있습니다.");
        }
        if (!issue.getReporter().getId().equals(tester.getId())) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "본인이 생성한 이슈만 삭제 요청할 수 있습니다.");
        }

        issue.setStatus(Status.DELETE_REQUEST);
        issueRepository.save(issue);
        return dtoConverter.createIssueResponseDto(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponseDto> getRemoveRequestIssues(Long signId) {
        Member admin = entityValidator.validateMember(signId);

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자만 삭제 요청 이슈를 조회할 수 있습니다.");
        }
        return issueRepository.findByStatusAndIsDeletedFalse(Status.DELETE_REQUEST).stream()
                .map(dtoConverter::createIssueResponseDto)
                .toList();
    }

    @Transactional
    public IssueResponseDto removeIssue(Long signId, IssueDeleteRequestDto issueDeleteRequestDto){
        Member admin = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueDeleteRequestDto.getIssueId());

        if(!admin.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자만 이슈를 삭제할 수 있습니다.");
        }
        if (!issue.getStatus().equals(Status.DELETE_REQUEST)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "삭제 요청된 이슈만 삭제할 수 있습니다.");
        }

        issue.setIsDeleted(true);
        issueRepository.save(issue);
        return dtoConverter.createIssueResponseDto(issue);
    }

    @Transactional
    public IssueResponseDto updateIssue(Long signId, IssueUpdateRequestDto issueUpdateRequestDto){
        Member tester = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueUpdateRequestDto.getIssueId());


        if (!issue.getReporter().getId().equals(tester.getId())) {
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "본인이 생성한 이슈만 수정할 수 있습니다.");
        }
        if (!tester.getRole().equals(Role.TESTER)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "TESTER만 이슈를 수정할 수 있습니다.");
        }

        issue.setDescription(issueUpdateRequestDto.getDescription());
        issue.setStatus(issueUpdateRequestDto.getStatus());
        issue.setCategory(issueUpdateRequestDto.getCategory());
        issueRepository.save(issue);
        return dtoConverter.createIssueResponseDto(issue);
    }

    @Transactional
    public IssueResponseDto reassignIssue(Long signId, IssueAssignRequestDto issueAssignRequestDto){
        Member assigner = entityValidator.validateMember(signId);
        Member assignee = entityValidator.validateMember(issueAssignRequestDto.getAssigneeId());
        Issue issue = entityValidator.validateIssue(issueAssignRequestDto.getIssueId());
        Project project = entityValidator.validateProject(issue.getProject().getId());
        entityValidator.isMemberOfProject(assignee, project);

        if(!assigner.getRole().equals(Role.DEV)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "개발자만 양도가 가능합니다.");
        }
        if(!issue.getAssignee().getId().equals(assigner.getId())){
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "본인이 담당한 이슈가 아닙니다.");
        }
        if(!assignee.getRole().equals(Role.DEV)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "개발자만 양도 받을 수 있습니다.");
        }

        issue.setAssignee(assignee);
        issueRepository.save(issue);
        return dtoConverter.createIssueResponseDto(issue);
    }


}
