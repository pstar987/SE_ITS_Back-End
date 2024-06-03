package com.se.its.domain.issue.application;


import com.se.its.domain.comment.application.CommentService;
import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.IssueCategory;
import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.domain.Status;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import com.se.its.domain.issue.dto.request.*;
import com.se.its.domain.issue.dto.response.IssueRecommendModelResponseDto;
import com.se.its.domain.issue.dto.response.IssueRecommendResponseDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.project.domain.Project;
import com.se.its.global.error.exceptions.BadRequestException;
import com.se.its.global.util.dto.DtoConverter;
import com.se.its.global.util.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.se.its.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final DtoConverter dtoConverter;
    private final EntityValidator entityValidator;
    private final CommentService commentService;

    private final String flaskApiUrl = "http://3.34.107.220:5000/api/v1/issue/issue_recommend";


    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public List<IssueRecommendResponseDto> recommendIssues(Long signId, Long issueId) {
        Member member = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueId);
        Project project = entityValidator.validateProject(issue.getProject().getId());
        entityValidator.isMemberOfProject(member, project);

        IssueRecommendRequestDto issueRecommendRequestDto = dtoConverter.createIssueRecommendRequestDto(issue);

        IssueRecommendModelResponseDto[] response = restTemplate.postForObject(
                flaskApiUrl,
                issueRecommendRequestDto,
                IssueRecommendModelResponseDto[].class
        );

        if (response == null) {
            throw new BadRequestException(MODEL_API_CALL_FAILED, "이슈 추천에 실패하였습니다.");
        }

        return Arrays.stream(response)
                .map(modelResponse -> {
                    Issue recommendedIssue = entityValidator.validateIssue(modelResponse.getIssue_id());
                    return dtoConverter.createIssueRecommendResponseDto(recommendedIssue, modelResponse.getScore());
                })
                .toList();
    }

    private void saveIssueToModel(Issue issue){
        IssueRecommendRequestDto issueRecommendRequestDto = dtoConverter.createIssueRecommendRequestDto(issue);
        IssueRecommendResponseDto[] response = restTemplate.postForObject(
                flaskApiUrl,
                issueRecommendRequestDto,
                IssueRecommendResponseDto[].class
        );

        if (response == null) {
            throw new BadRequestException(MODEL_API_CALL_FAILED, "이슈 등록에 실패했습니다.");
        }
    }

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
        saveIssueToModel(savedIssue);

        return dtoConverter.createIssueResponseDto(savedIssue);

    }

    @Transactional(readOnly = true)
    public IssueResponseDto getIssue(Long signId, Long issueId){
        Member member = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueId);
        Project project = entityValidator.validateProject(issue.getProject().getId());

        entityValidator.isMemberOfProject(member, project);

        return dtoConverter.createIssueResponseDto(issue);

    }

    @Transactional(readOnly = true)
    public List<IssueResponseDto> getIssues(Long signId, Long projectId) {
        Member member = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);

        entityValidator.isMemberOfProject(member, project);

        return issueRepository.findByProjectIdAndIsDeletedFalse(project.getId()).stream()
                .map(dtoConverter::createIssueResponseDto).toList();

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


    //dev가 본인에게 할당된 이슈만 확인할 수 있게
    @Transactional(readOnly = true)
    public List<IssueResponseDto> getDevIssues(Long signId, Long projectId) {
        Member developer = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        entityValidator.isMemberOfProject(developer, project);

        if (!developer.getRole().equals(Role.DEV)) {
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "개발자만 본인에게 할당된 이슈를 확인할 수 있습니다.");
        }
        return issueRepository.findByAssigneeIdAndProjectIdAndIsDeletedFalse(developer.getId(), project.getId()).stream()
                .map(dtoConverter::createIssueResponseDto).toList();
    }

    //tester가 본인이 생성한 이슈만 확인할 수 있게
    @Transactional(readOnly = true)
    public List<IssueResponseDto> getTesterIssues(Long signId, Long projectId) {
        Member tester = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        entityValidator.isMemberOfProject(tester, project);

        if (!tester.getRole().equals(Role.TESTER)) {
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "테스터만 본인이 생성한 이슈를 조회할 수 있습니다.");
        }

        return issueRepository.findByReporterIdAndProjectIdAndIsDeletedFalse(tester.getId(), project.getId()).stream()
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

        if (!entityValidator.isAdminOrPl(plMember)) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자와 프로젝트 리더만 이슈를 할당할 수 있습니다.");
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

        CommentCreateRequestDto commentCreateRequestDto = dtoConverter.createCommentRequestDto(
                issue,
                assignee.getName() + "가 해당 이슈에 할당되었습니다.");

        commentService.createComment(1L, commentCreateRequestDto);

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

        CommentCreateRequestDto commentCreateRequestDto = dtoConverter.createCommentRequestDto(
                issue,
                tester.getName() + "가 이슈 삭제를 요청했습니다.");

        commentService.createComment(1L, commentCreateRequestDto);
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
            throw new BadRequestException(INVALID_REQUEST_STATUS, "삭제 요청된 이슈만 삭제할 수 있습니다.");
        }

        issue.setIsDeleted(true);
        issueRepository.save(issue);
        return dtoConverter.createIssueResponseDto(issue);
    }

    @Transactional
    public IssueResponseDto updateIssue(Long signId, IssueUpdateRequestDto issueUpdateRequestDto){
        Member member = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueUpdateRequestDto.getIssueId());
        Project project = entityValidator.validateProject(issue.getProject().getId());
        entityValidator.isMemberOfProject(member, project);

        if(member.getRole().equals(Role.DEV)){
            throw new BadRequestException(INVALID_REQUEST_STATUS, "개발자는 이슈 업데이트가 불가합니다.");
        }
        if(member.getRole().equals(Role.TESTER)){
            if(!issue.getReporter().getId().equals(member.getId())){
                throw new BadRequestException(ROW_DOES_NOT_EXIST, "테스터는 본인의 이슈만 업데이트 가능합니다.");
            }
        }
        issue.setDescription(issueUpdateRequestDto.getDescription());
        issue.setStatus(issueUpdateRequestDto.getStatus());
        issue.setCategory(issueUpdateRequestDto.getCategory());
        issue.setPriority(issueUpdateRequestDto.getPriority());

        CommentCreateRequestDto commentCreateRequestDto = dtoConverter.createCommentRequestDto(
                issue,
                member.getName() + "가 이슈를 업데이트하였습니다.");

        commentService.createComment(1L, commentCreateRequestDto);

        issueRepository.save(issue);
        return dtoConverter.createIssueResponseDto(issue);
    }

    @Transactional
    public IssueResponseDto updateIssueDev(Long signId, IssueStatusUpdateRequestDto issueStatusUpdateRequestDto){
        Member member = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueStatusUpdateRequestDto.getIssueId());
        Project project = entityValidator.validateProject(issue.getProject().getId());
        entityValidator.isMemberOfProject(member, project);

        if(!member.getRole().equals(Role.DEV)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "개발자만 상태변경 기능을 사용가능합니다.");
        }
        if (!issue.getAssignee().getId().equals(member.getId())) {
            throw new BadRequestException(ROW_DOES_NOT_EXIST, "본인에게 할당된 이슈의 상태만 수정할 수 있습니다.");
        }

        issue.setStatus(issueStatusUpdateRequestDto.getStatus());

        String message;
        if(issueStatusUpdateRequestDto.getStatus().equals(Status.RESOLVED)){
            issue.setFixer(member);
            message = member.getName() + "님이 이슈를 해결하였습니다.";
        } else{
            message = member.getName() + "님이 이슈를 " + issueStatusUpdateRequestDto.getStatus().name() + " 상태로 변경하였습니다.";
        }

        issueRepository.save(issue);

        CommentCreateRequestDto commentCreateRequestDto = dtoConverter.createCommentRequestDto(
                issue,
                message);
        commentService.createComment(1L, commentCreateRequestDto);



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

        CommentCreateRequestDto commentCreateRequestDto = dtoConverter.createCommentRequestDto(
                issue,
                assignee.getName() + "가 해당 이슈에 할당되었습니다.");

        commentService.createComment(1L, commentCreateRequestDto);

        return dtoConverter.createIssueResponseDto(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponseDto> searchIssues(Long signId, IssueCategory category, Long projectId, String keyword) {
        Member member = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        entityValidator.isMemberOfProject(member, project);


        List<Issue> issues = switch (category) {
            case TITLE -> issueRepository.findByTitleContainingAndIsDeletedFalse(keyword);
            case STATUS -> issueRepository.findByStatusAndIsDeletedFalse(Status.valueOf(keyword.toUpperCase()));
            case PRIORITY -> issueRepository.findByPriorityAndIsDeletedFalse(Priority.valueOf(keyword.toUpperCase()));
            case ASSIGNEE -> issueRepository.findByAssigneeNameOrSignIdAndIsDeletedFalse(keyword);
        };


        return issues.stream()
                .map(dtoConverter::createIssueResponseDto)
                .toList();
    }


}
