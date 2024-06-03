package com.se.its.domain.project.application;

import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.ProjectMember;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.domain.project.dto.request.ProjectCreateRequestDto;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.global.error.exceptions.BadRequestException;
import com.se.its.global.util.dto.DtoConverter;
import com.se.its.global.util.validator.EntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ComponentScan(basePackages = {"com.se.its.global.util", "com.se.its.global.error"})
class ProjectServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    private EntityValidator entityValidator;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository, projectMemberRepository, null, dtoConverter, entityValidator);
    }

    @Test
    @DisplayName("유효한 관리자와 유효한 프로젝트 리더 및 멤버를 사용하여 프로젝트를 생성할 수 있는가")
    @Transactional
    void testCreateProject_ValidAdminAndLeader_ShouldCreateProject() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        ProjectCreateRequestDto projectCreateRequestDto = ProjectCreateRequestDto.builder()
                .name("Test Project")
                .memberIds(List.of(projectLeader.getId(), member.getId()))
                .build();

        // When
        ProjectResponseDto responseDto = projectService.createProject(admin.getId(), projectCreateRequestDto);

        // Then
        assertThat(responseDto.getName()).isEqualTo("Test Project");
        assertThat(responseDto.getLeaderId()).isEqualTo(projectLeader.getId());
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 프로젝트를 생성하려고 할 때 예외가 발생하는가")
    @Transactional
    void testCreateProject_NonAdmin_ShouldThrowBadRequestException() {
        // Given
        Member nonAdmin = Member.builder().signId("nonAdmin").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(nonAdmin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member member = Member.builder().signId("member").password("password").role(Role.TESTER).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        ProjectCreateRequestDto projectCreateRequestDto = ProjectCreateRequestDto.builder()
                .name("Test Project")
                .memberIds(List.of(projectLeader.getId(), member.getId(), nonAdmin.getId()))
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.createProject(nonAdmin.getId(), projectCreateRequestDto));
        assertThat(exception.getMessage()).isEqualTo("관리자가 아니기 때문에 프로젝트를 생성할 수 없습니다.");
    }

    @Test
    @DisplayName("프로젝트 리더가 한 명이 아닌 경우 예외가 발생하는가")
    @Transactional
    void testCreateProject_MultipleProjectLeaders_ShouldThrowBadRequestException() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member projectLeader1 = Member.builder().signId("projectLeader1").password("password").role(Role.PL).name("Project Leader 1").isDeleted(false).build();
        memberRepository.save(projectLeader1);

        Member projectLeader2 = Member.builder().signId("projectLeader2").password("password").role(Role.PL).name("Project Leader 2").isDeleted(false).build();
        memberRepository.save(projectLeader2);

        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        ProjectCreateRequestDto projectCreateRequestDto = ProjectCreateRequestDto.builder()
                .name("Test Project")
                .memberIds(List.of(projectLeader1.getId(), projectLeader2.getId(), member.getId()))
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.createProject(admin.getId(), projectCreateRequestDto));
        assertThat(exception.getMessage()).isEqualTo("프로젝트 리더 한명 필요합니다.");
    }
}
