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
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.global.error.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.se.its.global.error.ErrorCode.INVALID_REQUEST_ROLE;
import static com.se.its.global.error.ErrorCode.ROW_DOES_NOT_EXIST;

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

        Member projectLeader = getUser(projectCreateRequestDto.getProjectLeaderId());


        Project project = Project.builder()
                .name(projectCreateRequestDto.getName())
                .isDeleted(false)
                .build();
        projectRepository.save(project);


        ProjectMember leaderProjectMember = ProjectMember.builder()
                .project(project)
                .member(projectLeader)
                .build();
        projectMemberRepository.save(leaderProjectMember);


        List<ProjectMember> projectMembers = projectCreateRequestDto.getMemberIds().stream()
                .map(memberId -> getUser(memberId))
                .map(member -> ProjectMember.builder()
                        .project(project)
                        .member(member)
                        .build())
                .collect(Collectors.toList());

        projectMemberRepository.saveAll(projectMembers);


        List<MemberResponseDto> memberResponseDtos = projectMemberRepository.findByProjectId(project.getId()).stream()
                .map(pm -> MemberResponseDto.builder()
                        .id(pm.getMember().getId())
                        .name(pm.getMember().getName())
                        .role(pm.getMember().getRole())
                        .isDeleted(pm.getMember().getIsDeleted())
                        .build())
                .collect(Collectors.toList());


        return ProjectResponseDto.builder()
                .projectId(project.getId())
                .name(project.getName())
                .members(memberResponseDtos)
                .build();

    }


    private Member getUser(Long targetId){
        return memberRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 사용자입니다."));
    }


}
