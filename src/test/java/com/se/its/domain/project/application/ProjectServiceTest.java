package com.se.its.domain.project.application;

import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
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
    @DisplayName("유효한 관리자와 프로젝트 리더를 제외한 멤버를 사용하여 프로젝트를 생성할 때 예외가 발생하는가")
    @Transactional
    void testCreateProject_ValidAdminAndWithoutLeader_ShouldThrowBadRequest() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        ProjectCreateRequestDto projectCreateRequestDto = ProjectCreateRequestDto.builder()
                .name("Test Project")
                .memberIds(List.of(member.getId()))
                .build();


        // Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.createProject(admin.getId(), projectCreateRequestDto));
        assertThat(exception.getMessage()).isEqualTo("프로젝트 리더 한명 필요합니다.");
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

    @Test
    @DisplayName("유효한 signId와 projectId로 프로젝트를 조회할 수 있는가")
    @Transactional
    void testGetProjectById_ValidIds_ShouldReturnProject() {
        // Given
        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project = Project.builder().leaderId(projectLeader.getId()).name("Test Project").isDeleted(false).build();
        projectRepository.save(project);

        ProjectMember projectMember = ProjectMember.builder().project(project).member(member).isDeleted(false).build();
        projectMemberRepository.save(projectMember);

        ProjectMember projectMember2 = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMember2);

        // When
        ProjectResponseDto responseDto = projectService.getProjectById(member.getId(), project.getId());

        // Then
        assertThat(responseDto.getName()).isEqualTo("Test Project");
        assertThat(responseDto.getId()).isEqualTo(project.getId());
    }
    @Test
    @DisplayName("유효하지 않은 signId로 프로젝트를 조회할 때 예외가 발생하는가")
    @Transactional
    void testGetProjectById_InvalidSignId_ShouldThrowBadRequestException() {
        // Given
        Long invalidSignId = 999L;

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberDev = ProjectMember.builder().project(project).member(member).isDeleted(false).build();
        projectMemberRepository.save(projectMemberDev);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.getProjectById(invalidSignId, project.getId()));
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 projectId로 프로젝트를 조회할 때 예외가 발생하는가")
    @Transactional
    void testGetProjectById_InvalidProjectId_ShouldThrowBadRequestException() {
        // Given
        Long invalidProjectId = 999L;

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberDev = ProjectMember.builder().project(project).member(member).isDeleted(false).build();
        projectMemberRepository.save(projectMemberDev);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.getProjectById(member.getId(), invalidProjectId));
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 프로젝트입니다.");
    }

    @Test
    @DisplayName("프로젝트 멤버가 아닌 사용자가 프로젝트를 조회할 때 예외가 발생하는가")
    @Transactional
    void testGetProjectById_NonMember_ShouldThrowBadRequestException() {
        // Given
        Member nonMember = Member.builder().signId("nonMember").password("password").role(Role.DEV).name("Non-Member").isDeleted(false).build();
        memberRepository.save(nonMember);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberDev = ProjectMember.builder().project(project).member(member).isDeleted(false).build();
        projectMemberRepository.save(projectMemberDev);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.getProjectById(nonMember.getId(), project.getId()));
        assertThat(exception.getMessage()).isEqualTo("해당 프로젝트의 멤버가 아닙니다.");
    }
    @Test
    @DisplayName("관리자가 모든 프로젝트를 조회할 수 있는가")
    @Transactional
    void testGetAllProject_AsAdmin_ShouldReturnAllProjects() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project1 = Project.builder().name("Project 1").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project1);

        Project project2 = Project.builder().name("Project 2").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project2);

        ProjectMember projectMember1 = ProjectMember.builder().project(project1).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMember1);

        ProjectMember projectMember2 = ProjectMember.builder().project(project2).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMember2);

        // When
        List<ProjectResponseDto> responseDtos = projectService.getAllProject(admin.getId());

        // Then
        assertThat(responseDtos).hasSize(2);
        assertThat(responseDtos).extracting("name").containsExactlyInAnyOrder("Project 1", "Project 2");
    }

    @Test
    @DisplayName("일반 멤버가 자신이 속한 프로젝트만 조회할 수 있는가")
    @Transactional
    void testGetAllProject_AsMember_ShouldReturnOnlyMemberProjects() {
        // Given
        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project1 = Project.builder().name("Project 1").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project1);

        Project project2 = Project.builder().name("Project 2").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project2);

        ProjectMember projectMember1 = ProjectMember.builder().project(project1).member(member).isDeleted(false).build();
        projectMemberRepository.save(projectMember1);

        ProjectMember projectMember2 = ProjectMember.builder().project(project2).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMember2);

        // When
        List<ProjectResponseDto> responseDtos = projectService.getAllProject(member.getId());

        // Then
        assertThat(responseDtos).hasSize(1);
        assertThat(responseDtos).extracting("name").containsExactly("Project 1");
    }

    @Test
    @DisplayName("프로젝트에 속하지 않은 일반 멤버가 조회할 때 빈 목록을 반환하는가")
    @Transactional
    void testGetAllProject_AsNonMember_ShouldReturnEmptyList() {
        // Given
        Member member = Member.builder().signId("member").password("password").role(Role.DEV).name("Member").isDeleted(false).build();
        memberRepository.save(member);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project1 = Project.builder().name("Project 1").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project1);

        ProjectMember projectMember1 = ProjectMember.builder().project(project1).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMember1);

        // When
        List<ProjectResponseDto> responseDtos = projectService.getAllProject(member.getId());

        // Then
        assertThat(responseDtos).isEmpty();
    }


    @Test
    @DisplayName("관리자가 프로젝트에 새로운 멤버를 추가할 수 있는가")
    @Transactional
    void testAddMember_AsAdmin_ShouldAddMember() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        Member newMember = Member.builder().signId("newMember").password("password").role(Role.DEV).name("New Member").isDeleted(false).build();
        memberRepository.save(newMember);

        ProjectMemberAddRequestDto requestDto = ProjectMemberAddRequestDto.builder()
                .addMemberId(newMember.getId())
                .build();

        // When
        ProjectResponseDto responseDto = projectService.addMember(admin.getId(), project.getId(), requestDto);

        // Then
        assertThat(responseDto.getId()).isEqualTo(project.getId());
        assertThat(projectMemberRepository.existsByMemberIdAndProjectIdAndIsDeletedFalse(newMember.getId(), project.getId())).isTrue();
    }

    @Test
    @DisplayName("프로젝트 리더가 프로젝트에 새로운 멤버를 추가할 수 있는가")
    @Transactional
    void testAddMember_AsProjectLeader_ShouldAddMember() {
        // Given
        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        Member newMember = Member.builder().signId("newMember").password("password").role(Role.DEV).name("New Member").isDeleted(false).build();
        memberRepository.save(newMember);

        ProjectMemberAddRequestDto requestDto = ProjectMemberAddRequestDto.builder()
                .addMemberId(newMember.getId())
                .build();

        // When
        ProjectResponseDto responseDto = projectService.addMember(projectLeader.getId(), project.getId(), requestDto);

        // Then
        assertThat(responseDto.getId()).isEqualTo(project.getId());
        assertThat(projectMemberRepository.existsByMemberIdAndProjectIdAndIsDeletedFalse(newMember.getId(), project.getId())).isTrue();
    }

    @Test
    @DisplayName("프로젝트에 이미 프로젝트 리더가 있을 때 새로운 프로젝트 리더를 추가하려고 하면 예외가 발생하는가")
    @Transactional
    void testAddMember_ExistingProjectLeader_ShouldThrowBadRequestException() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        Member newProjectLeader = Member.builder().signId("newProjectLeader").password("password").role(Role.PL).name("New Project Leader").isDeleted(false).build();
        memberRepository.save(newProjectLeader);

        ProjectMemberAddRequestDto requestDto = ProjectMemberAddRequestDto.builder()
                .addMemberId(newProjectLeader.getId())
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.addMember(admin.getId(), project.getId(), requestDto));
        assertThat(exception.getMessage()).isEqualTo("프로젝트에 이미 PL이 존재합니다.");
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 프로젝트에 멤버를 추가하려고 하면 예외가 발생하는가")
    @Transactional
    void testAddMember_NonAdmin_ShouldThrowBadRequestException() {
        // Given
        Member nonAdmin = Member.builder().signId("nonAdmin").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(nonAdmin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberNonAdmin = ProjectMember.builder().project(project).member(nonAdmin).isDeleted(false).build();
        projectMemberRepository.save(projectMemberNonAdmin);

        Member newMember = Member.builder().signId("newMember").password("password").role(Role.DEV).name("New Member").isDeleted(false).build();
        memberRepository.save(newMember);

        ProjectMemberAddRequestDto requestDto = ProjectMemberAddRequestDto.builder()
                .addMemberId(newMember.getId())
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.addMember(nonAdmin.getId(), project.getId(), requestDto));
        assertThat(exception.getMessage()).isEqualTo("관리자나 프로젝트 리더가 아닙니다.");
    }


    @Test
    @DisplayName("관리자가 프로젝트에서 멤버를 제거할 수 있는가")
    @Transactional
    void testRemoveMember_AsAdmin_ShouldRemoveMember() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member memberToRemove = Member.builder().signId("memberToRemove").password("password").role(Role.TESTER).name("Member To Remove").isDeleted(false).build();
        memberRepository.save(memberToRemove);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberToRemove = ProjectMember.builder().project(project).member(memberToRemove).isDeleted(false).build();
        projectMemberRepository.save(projectMemberToRemove);

        ProjectMemberRemoveRequestDto requestDto = ProjectMemberRemoveRequestDto.builder()
                .removeMemberId(memberToRemove.getId())
                .build();

        // When
        ProjectResponseDto responseDto = projectService.removeMember(admin.getId(), project.getId(), requestDto);

        // Then
        assertThat(responseDto.getId()).isEqualTo(project.getId());
        assertThat(projectMemberRepository.existsByMemberIdAndProjectIdAndIsDeletedFalse(memberToRemove.getId(), project.getId())).isFalse();
    }

    @Test
    @DisplayName("프로젝트 리더가 프로젝트에서 멤버를 제거할 수 있는가")
    @Transactional
    void testRemoveMember_AsProjectLeader_ShouldRemoveMember() {
        // Given
        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member memberToRemove = Member.builder().signId("memberToRemove").password("password").role(Role.DEV).name("Member To Remove").isDeleted(false).build();
        memberRepository.save(memberToRemove);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberToRemove = ProjectMember.builder().project(project).member(memberToRemove).isDeleted(false).build();
        projectMemberRepository.save(projectMemberToRemove);

        ProjectMemberRemoveRequestDto requestDto = ProjectMemberRemoveRequestDto.builder()
                .removeMemberId(memberToRemove.getId())
                .build();

        // When
        ProjectResponseDto responseDto = projectService.removeMember(projectLeader.getId(), project.getId(), requestDto);

        // Then
        assertThat(responseDto.getId()).isEqualTo(project.getId());
        assertThat(projectMemberRepository.existsByMemberIdAndProjectIdAndIsDeletedFalse(memberToRemove.getId(), project.getId())).isFalse();
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 프로젝트에서 멤버를 제거하려고 하면 예외가 발생하는가")
    @Transactional
    void testRemoveMember_NonAdmin_ShouldThrowBadRequestException() {
        // Given
        Member nonAdmin = Member.builder().signId("nonAdmin").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(nonAdmin);

        Member projectLeader = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Member memberToRemove = Member.builder().signId("memberToRemove").password("password").role(Role.TESTER).name("Member To Remove").isDeleted(false).build();
        memberRepository.save(memberToRemove);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberToRemove = ProjectMember.builder().project(project).member(memberToRemove).isDeleted(false).build();
        projectMemberRepository.save(projectMemberToRemove);

        ProjectMember projectMemberNonAdmin = ProjectMember.builder().project(project).member(nonAdmin).isDeleted(false).build();
        projectMemberRepository.save(projectMemberNonAdmin);

        ProjectMemberRemoveRequestDto requestDto = ProjectMemberRemoveRequestDto.builder()
                .removeMemberId(memberToRemove.getId())
                .build();

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> projectService.removeMember(nonAdmin.getId(), project.getId(), requestDto));
        assertThat(exception.getMessage()).isEqualTo("관리자나 프로젝트 리더가 아닙니다.");
    }


}
