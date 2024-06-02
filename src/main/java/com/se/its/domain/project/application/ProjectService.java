package com.se.its.domain.project.application;


import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.ProjectMember;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.domain.project.dto.request.ProjectCreateRequestDto;
import com.se.its.domain.project.dto.request.ProjectMemberAddRequestDto;
import com.se.its.domain.project.dto.request.ProjectMemberRemoveRequestDto;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.global.error.exceptions.BadRequestException;
import com.se.its.global.util.dto.DtoConverter;
import com.se.its.global.util.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.se.its.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final IssueRepository issueRepository;
    private final DtoConverter dtoConverter;
    private final EntityValidator entityValidator;

    @Transactional
    public ProjectResponseDto createProject(Long signId, ProjectCreateRequestDto projectCreateRequestDto){
        Member admin = entityValidator.validateMember(signId);
        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아니기 때문에 프로젝트를 생성할 수 없습니다.");
        }

        //멤버 유효성 검사
        List<Member> validMembers = projectCreateRequestDto.getMemberIds().stream()
                .map(entityValidator::validateMember)
                .toList();

        List<Member> plMembers = validMembers.stream()
                .filter(member -> member.getRole().equals(Role.PL))
                .toList();

        if (plMembers.size() != 1) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "프로젝트 리더 한명 필요합니다.");
        }

        Member plMember = plMembers.get(0);

        Project project = Project.builder()
                .name(projectCreateRequestDto.getName())
                .isDeleted(false)
                .leaderId(plMember.getId())
                .build();
        projectRepository.save(project);


        List<ProjectMember> projectMembers = validMembers.stream()
                .map(member -> ProjectMember.builder()
                        .project(project)
                        .member(member)
                        .isDeleted(false)
                        .build())
                .collect(Collectors.toList());

        projectMemberRepository.saveAll(projectMembers);


        return dtoConverter.createProjectResponseDto(project);

    }

    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(Long signId, Long projectId) {
        Member member = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        entityValidator.isMemberOfProject(member, project);

        //프로젝트 멤버 조회
        return dtoConverter.createProjectResponseDto(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProject(Long id){
        Member member = entityValidator.validateMember(id);

        List<Project> projects;
        if (member.getRole().equals(Role.ADMIN)) {
            // 관리자는 모든 프로젝트를 조회
            projects = projectRepository.findByIsDeletedIsFalse();
        } else {
            // 일반 멤버는 본인이 속한 프로젝트만 조회
            projects = projectMemberRepository.findByMemberIdAndIsDeletedFalse(member.getId()).stream()
                    .map(ProjectMember::getProject)
                    .filter(project -> !project.getIsDeleted())
                    .collect(Collectors.toList());
        }

        return projects.stream()
                .map(dtoConverter::createProjectResponseDto)
                .toList();

    }


    @Transactional
    public ProjectResponseDto addMember(Long signId, Long projectId, ProjectMemberAddRequestDto projectMemberAddRequestDto){
        Member admin = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        Member newMember = entityValidator.validateMember(projectMemberAddRequestDto.getAddMemberId());
        entityValidator.isMemberOfProject(admin, project);

        if (project.getLeaderId() != null && newMember.getRole().equals(Role.PL)) {
            throw new BadRequestException(ROW_ALREADY_EXIST, "프로젝트에 이미 PL이 존재합니다.");
        }

        if(admin.getRole().equals(Role.ADMIN)){
            addProjectMember(project, newMember);
            if(newMember.getRole().equals(Role.PL)){
                project.setLeaderId(newMember.getId());
            }
        }else if(admin.getRole().equals(Role.PL)) {
            if (newMember.getRole().equals(Role.PL)) {
                throw new BadRequestException(INVALID_REQUEST_ROLE, "프로젝트 리더는 다른 프로젝트 리더를 추가할 수 없습니다.");
            }
            addProjectMember(project, newMember);
        }
        return dtoConverter.createProjectResponseDto(project);
    }

    @Transactional
    public ProjectResponseDto removeMember(Long signId, Long projectId, ProjectMemberRemoveRequestDto projectMemberRemoveRequestDto){
        Member admin = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        Member removeMember = entityValidator.validateMember(projectMemberRemoveRequestDto.getRemoveMemberId());
        entityValidator.isMemberOfProject(admin, project);

        if(admin.getRole().equals(Role.ADMIN)){
            removeProjectMember(project, removeMember);
            if(removeMember.getRole().equals(Role.PL)){
                project.setLeaderId(null);
            }
        }else if(admin.getRole().equals(Role.PL)){
            if (removeMember.getRole().equals(Role.PL)) {
                throw new BadRequestException(INVALID_REQUEST_ROLE, "프로젝트 리더는 프로젝트 리더를 삭제할 수 없습니다.");
            }
            removeProjectMember(project, removeMember);
        }
        return dtoConverter.createProjectResponseDto(project);
    }

    @Transactional
    public void removeProject(Long signId, Long projectId){
        Member admin = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        if(!admin.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }
        project.setIsDeleted(true);
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjectIdAndIsDeletedFalse(projectId);
        List<Issue> issues = issueRepository.findByProjectIdAndIsDeletedFalse(projectId);
        projectMembers.forEach(pm -> pm.setIsDeleted(true));
        issues.forEach(issue -> issue.setIsDeleted(true));
    }



    private void addProjectMember(Project project, Member newMember) {
        // 프로젝트에 이미 존재하는 멤버인지 확인
        if(newMember.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자는 프로젝트에 할당되지 않습니다.");
        }
        boolean isAlreadyMember = projectMemberRepository.existsByMemberIdAndProjectIdAndIsDeletedFalse(newMember.getId(), project.getId());
        if (isAlreadyMember) {
            throw new BadRequestException(ROW_ALREADY_EXIST, "해당 사용자는 이미 이 프로젝트의 멤버입니다.");
        }

        // 새로운 멤버를 프로젝트에 추가
        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .member(newMember)
                .isDeleted(false)
                .build();
        projectMemberRepository.save(projectMember);
    }

    private void removeProjectMember(Project project, Member removeMember) {
        if(removeMember.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자는 프로젝트에 할당되지 않습니다.");
        }
        // 프로젝트에 이미 존재하는 멤버인지 확인
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndMemberIdAndIsDeletedFalse(project.getId(), removeMember.getId())
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "해당 프로젝트의 멤버가 아닙니다."));

        projectMember.setIsDeleted(true);
    }





}
