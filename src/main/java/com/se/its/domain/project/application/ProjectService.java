package com.se.its.domain.project.application;


import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.ProjectMember;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.domain.project.dto.request.ProjectCreateRequestDto;
import com.se.its.domain.project.dto.request.ProjectMemberAddRequestDto;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.global.error.exceptions.BadRequestException;
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
    private final MemberRepository memberRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public ProjectResponseDto createProject(Long id, ProjectCreateRequestDto projectCreateRequestDto){
        Member admin = getUser(id);
        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아니기 때문에 프로젝트를 생성할 수 없습니다.");
        }

        //멤버 유효성 검사
        List<Member> validMembers = projectCreateRequestDto.getMemberIds().stream()
                .map(memberId -> getUser(memberId))
                .collect(Collectors.toList());

        boolean hasPL = validMembers.stream().anyMatch(member -> member.getRole().equals(Role.PL));
        if (!hasPL) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "프로젝트 리더가 설정되지 않았습니다.");
        }


        Project project = Project.builder()
                .name(projectCreateRequestDto.getName())
                .isDeleted(false)
                .build();
        projectRepository.save(project);


        List<ProjectMember> projectMembers = validMembers.stream()
                .map(member -> ProjectMember.builder()
                        .project(project)
                        .member(member)
                        .build())
                .collect(Collectors.toList());

        projectMemberRepository.saveAll(projectMembers);


        return createProjectResponseDto(project);

    }

    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(Long signId, Long projectId) {
        Member member = getUser(signId);
        Project project = getProject(projectId);

        boolean isMemberOfProject = projectMemberRepository.existsByMemberAndProject(member, project);

        //admin일 경우 모든 프로젝트 조회 가능
        if(!member.getRole().equals(Role.ADMIN)){
            if (!isMemberOfProject) {
                throw new BadRequestException(ROW_DOES_NOT_EXIST, "해당 프로젝트의 멤버가 아닙니다.");
            }
        }

        //프로젝트 멤버 조회
        return createProjectResponseDto(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProject(Long id){
        Member member = getUser(id);

        List<Project> projects;
        if (member.getRole().equals(Role.ADMIN)) {
            // 관리자는 모든 프로젝트를 조회
            projects = projectRepository.findAll();
        } else {
            // 일반 멤버는 본인이 속한 프로젝트만 조회
            projects = projectMemberRepository.findByMember(member).stream()
                    .map(ProjectMember::getProject)
                    .collect(Collectors.toList());
        }

        return projects.stream()
                .map(project -> {
                    return createProjectResponseDto(project);
                })
                .collect(Collectors.toList());

    }


    @Transactional
    public ProjectResponseDto addMember(Long signId, Long projectId, ProjectMemberAddRequestDto projectMemberAddRequestDto){
        Member admin = getUser(signId);
        Project project = getProject(projectId);
        Member newMember = getUser(projectMemberAddRequestDto.getAddMemberId());
        isAdminOrPL(admin);

        if(admin.getRole().equals(Role.ADMIN)){
            addProjectMember(project, newMember);
        }else{
            boolean isMemberOfProject = projectMemberRepository.existsByMemberAndProject(admin, project);
            if (!isMemberOfProject) {
                throw new BadRequestException(ROW_DOES_NOT_EXIST, "해당 프로젝트의 멤버가 아니기 때문에 권한이 없습니다.");
            }
            addProjectMember(project, newMember);
        }
        return createProjectResponseDto(project);

    }


    private Member getUser(Long targetId){
        return memberRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 사용자입니다."));
    }

    private Project getProject(Long projectId){
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "프로젝트가 존재하지 않습니다."));
    }

    private void isAdminOrPL(Member member) {
        if (!(member.getRole().equals(Role.ADMIN) || member.getRole().equals(Role.PL))) {
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자 또는 프로젝트 리더가 아닙니다.");
        }
    }

    private void addProjectMember(Project project, Member newMember) {
        // 프로젝트에 이미 존재하는 멤버인지 확인
        boolean isAlreadyMember = projectMemberRepository.existsByMemberAndProject(newMember, project);
        if (isAlreadyMember) {
            throw new BadRequestException(ROW_ALREADY_EXIST, "해당 사용자는 이미 이 프로젝트의 멤버입니다.");
        }

        // 새로운 멤버를 프로젝트에 추가
        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .member(newMember)
                .build();
        projectMemberRepository.save(projectMember);
    }

    private ProjectResponseDto createProjectResponseDto(Project project) {
        List<MemberResponseDto> memberResponseDtos = projectMemberRepository.findByProjectId(project.getId()).stream()
                .map(pm -> MemberResponseDto.builder()
                        .id(pm.getMember().getId())
                        .name(pm.getMember().getName())
                        .role(pm.getMember().getRole())
                        .isDeleted(pm.getMember().getIsDeleted())
                        .build())
                .collect(Collectors.toList());

        return ProjectResponseDto.builder()
                .id(project.getId())
                .name(project.getName())
                .members(memberResponseDtos)
                .build();
    }




}
