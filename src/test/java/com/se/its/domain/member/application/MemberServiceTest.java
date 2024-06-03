package com.se.its.domain.member.application;

import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.domain.Status;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.dto.request.MemberDeleteRequestDto;
import com.se.its.domain.member.dto.request.MemberSignInRequestDto;
import com.se.its.domain.member.dto.request.MemberSignUpRequestDto;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.ProjectMember;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.global.error.exceptions.BadRequestException;
import com.se.its.global.error.exceptions.UnauthorizedException;
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
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    private EntityValidator entityValidator;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository, projectMemberRepository, issueRepository, dtoConverter, entityValidator);
    }

    //createAdmin
    @Test
    @DisplayName("createAdmin을 통해 관리자 생성이 정상적으로 작동하는가")
    @Transactional
    void testAdminCreate_ValidSignId_ShouldCreateAdmin() {
        // Given
        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto("adminSignId", "password", "Admin Name", Role.ADMIN);

        // When
        MemberResponseDto responseDto = memberService.adminCreate(requestDto);

        // Then
        assertThat(responseDto.getSignId()).isEqualTo(requestDto.getSignId());
        assertThat(responseDto.getRole()).isEqualTo(requestDto.getRole());
    }

    @Test
    @DisplayName("중복된 signId로 관리자를 생성하면 예외가 발생하는가")
    @Transactional
    void testAdminCreate_DuplicateSignId_ShouldThrowBadRequestException() {
        // Given
        Member existingAdmin = Member.builder().signId("duplicateSignId").password("password").role(Role.ADMIN).name("Existing Admin").isDeleted(false).build();
        memberRepository.save(existingAdmin);

        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto("duplicateSignId", "password", "Admin Name", Role.ADMIN);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.adminCreate(requestDto));
        assertThat(exception.getMessage()).isEqualTo("중복되는 ID가 존재합니다.");
    }

    @Test
    @DisplayName("유효한 관리자와 중복되지 않은 signId로 회원가입이 정상적으로 작동하는가")
    @Transactional
    void testSignUp_ValidAdminAndUniqueSignId_ShouldSaveMember() {
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin Name").isDeleted(false).build();
        memberRepository.save(admin);

        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto("uniqueSignId", "password", "name", Role.DEV);

        MemberResponseDto responseDto = memberService.signUp(admin.getId(), requestDto);

        assertThat(responseDto.getSignId()).isEqualTo(requestDto.getSignId());
        assertThat(responseDto.getRole()).isEqualTo(requestDto.getRole());
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 회원가입을 시도하면 예외가 발생하는가")
    @Transactional
    void testSignUp_NonAdmin_ShouldThrowBadRequestException() {
        // Given
        Member user = Member.builder().signId("user").password("password").role(Role.DEV).name("User Name").isDeleted(false).build();
        memberRepository.save(user);

        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto("uniqueSignId", "password", "name", Role.DEV);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.signUp(user.getId(), requestDto));
        assertThat(exception.getMessage()).isEqualTo("관리자가 아닙니다.");
    }

    @Test
    @DisplayName("중복된 signId로 회원가입을 시도하면 예외가 발생하는가")
    @Transactional
    void testSignUp_DuplicateSignId_ShouldThrowBadRequestException() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin Name").isDeleted(false).build();
        memberRepository.save(admin);

        Member existingMember = Member.builder().signId("duplicateSignId").password("password").role(Role.DEV).name("Existing User").isDeleted(false).build();
        memberRepository.save(existingMember);

        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto("duplicateSignId", "password", "name", Role.DEV);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.signUp(admin.getId(), requestDto));
        assertThat(exception.getMessage()).isEqualTo("중복되는 ID가 존재합니다.");
    }

    @Test
    @DisplayName("일반 회원 생성으로 관리자를 생성하려고 하면 예외가 발생하는가")
    @Transactional
    void testSignUp_CreateAdmin_ShouldThrowBadRequestException() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin Name").isDeleted(false).build();
        memberRepository.save(admin);

        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto("newAdmin", "password", "name", Role.ADMIN);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.signUp(admin.getId(), requestDto));
        assertThat(exception.getMessage()).isEqualTo("관리자를 생성할 수 없습니다.");
    }



    //signIn
    @Test
    @DisplayName("유효한 사용자와 비밀번호로 로그인할 수 있는가")
    @Transactional
    void testSignIn_ValidCredentials_ShouldReturnMemberResponseDto() {
        // Given
        Member member = Member.builder()
                .signId("validUser")
                .password("validPassword")
                .role(Role.DEV)
                .name("Valid User")
                .isDeleted(false)
                .build();
        memberRepository.save(member);

        MemberSignInRequestDto requestDto = new MemberSignInRequestDto("validUser", "validPassword");

        // When
        MemberResponseDto responseDto = memberService.signIn(requestDto);

        // Then
        assertThat(responseDto.getSignId()).isEqualTo(requestDto.getSignId());
        assertThat(responseDto.getRole()).isEqualTo(member.getRole());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인하려고 할 때 예외가 발생하는가")
    @Transactional
    void testSignIn_NonExistentUser_ShouldThrowBadRequestException() {
        // Given
        MemberSignInRequestDto requestDto = new MemberSignInRequestDto("nonExistentUser", "somePassword");

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.signIn(requestDto));
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호로 로그인하려고 할 때 예외가 발생하는가")
    @Transactional
    void testSignIn_InvalidPassword_ShouldThrowUnauthorizedException() {
        // Given
        Member member = Member.builder()
                .signId("validUser")
                .password("correctPassword")
                .role(Role.DEV)
                .name("Valid User")
                .isDeleted(false)
                .build();
        memberRepository.save(member);

        MemberSignInRequestDto requestDto = new MemberSignInRequestDto("validUser", "wrongPassword");

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> memberService.signIn(requestDto));
        assertThat(exception.getMessage()).isEqualTo("유효하지 않은 비밀번호입니다.");
    }

    // findMemberById
    @Test
    @DisplayName("유효한 signId로 회원을 조회할 수 있는가")
    @Transactional
    void testFindMemberById_ValidSignId_ShouldReturnMember() {
        // Given
        Member member = Member.builder()
                .signId("validUser")
                .password("password")
                .role(Role.DEV)
                .name("Valid User")
                .isDeleted(false)
                .build();
        memberRepository.save(member);

        // When
        MemberResponseDto responseDto = memberService.findMemberById(member.getId());

        // Then
        assertThat(responseDto.getSignId()).isEqualTo(member.getSignId());
        assertThat(responseDto.getRole()).isEqualTo(member.getRole());
    }

    @Test
    @DisplayName("유효하지 않은 signId로 회원을 조회할 때 예외가 발생하는가")
    @Transactional
    void testFindMemberById_InvalidSignId_ShouldThrowBadRequestException() {
        // Given
        Long invalidSignId = 999L;

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.findMemberById(invalidSignId));
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 사용자입니다."); // assuming this is the message in validateMember method
    }

    // findAllMembers
    @Test
    @DisplayName("관리자가 모든 회원 목록을 조회할 수 있는가")
    @Transactional
    void testFindAllMembers_AsAdmin_ShouldReturnAllMembersExceptAdmins() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member user1 = Member.builder().signId("user1").password("password").role(Role.DEV).name("User One").isDeleted(false).build();
        memberRepository.save(user1);

        Member user2 = Member.builder().signId("user2").password("password").role(Role.PL).name("User Two").isDeleted(false).build();
        memberRepository.save(user2);

        // When
        List<MemberResponseDto> responseDtos = memberService.findAllMembers(admin.getId());

        // Then
        assertThat(responseDtos).hasSize(2); // user1 and user2 should be returned, not admin
        assertThat(responseDtos).extracting("signId").containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 모든 회원 목록을 조회하려고 할 때 예외가 발생하는가")
    @Transactional
    void testFindAllMembers_AsNonAdmin_ShouldThrowBadRequestException() {
        // Given
        Member nonAdmin = Member.builder().signId("user").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(nonAdmin);

        Member projectLeader = Member.builder().signId("user").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberDev = ProjectMember.builder().project(project).member(nonAdmin).isDeleted(false).build();
        projectMemberRepository.save(projectMemberDev);


        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.findAllMembers(nonAdmin.getId()));
        assertThat(exception.getMessage()).isEqualTo("관리자나 프로젝트 리더가 아닙니다.");
    }

    // Test for findMembersByAdminAndPL
    @Test
    @DisplayName("관리자가 특정 프로젝트의 회원 목록을 조회할 수 있는가")
    @Transactional
    void testFindMembersByAdminAndPL_AsAdmin_ShouldReturnProjectMembers() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);


        Member user1 = Member.builder().signId("user1").password("password").role(Role.DEV).name("User One").isDeleted(false).build();
        memberRepository.save(user1);
        Member user2 = Member.builder().signId("user2").password("password").role(Role.TESTER).name("User Two").isDeleted(false).build();
        memberRepository.save(user2);
        Member projectLeader = Member.builder().signId("user2").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project = Project.builder().name("Test Project").leaderId(projectLeader.getId()).isDeleted(false).build();
        projectRepository.save(project);

        ProjectMember pm1 = ProjectMember.builder().project(project).member(user1).isDeleted(false).build();
        projectMemberRepository.save(pm1);
        ProjectMember pmPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(pmPl);

        // When
        List<MemberResponseDto> responseDtos = memberService.findMembersByAdminAndPL(admin.getId(), project.getId());

        // Then
        assertThat(responseDtos).hasSize(2); // user1 and user2 should be returned
        assertThat(responseDtos).extracting("signId").containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 특정 프로젝트의 회원 목록을 조회하려고 할 때 예외가 발생하는가")
    @Transactional
    void testFindMembersByAdminAndPL_AsNonAdmin_ShouldThrowBadRequestException() {
        Member nonAdmin = Member.builder().signId("user").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(nonAdmin);

        Member projectLeader = Member.builder().signId("user").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(projectLeader);

        Project project = Project.builder().name("Test Project").isDeleted(false).leaderId(projectLeader.getId()).build();
        projectRepository.save(project);

        ProjectMember projectMemberPl = ProjectMember.builder().project(project).member(projectLeader).isDeleted(false).build();
        projectMemberRepository.save(projectMemberPl);

        ProjectMember projectMemberDev = ProjectMember.builder().project(project).member(nonAdmin).isDeleted(false).build();
        projectMemberRepository.save(projectMemberDev);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.findMembersByAdminAndPL(nonAdmin.getId(), project.getId()));
        assertThat(exception.getMessage()).isEqualTo("관리자나 프로젝트 리더가 아닙니다.");
    }

    // Test for findMembersByRole
    @Test
    @DisplayName("관리자가 특정 프로젝트의 특정 역할을 가진 회원 목록을 조회할 수 있는가")
    @Transactional
    void testFindMembersByRole_AsAdmin_ShouldReturnProjectMembersByRole() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member user1 = Member.builder().signId("user1").password("password").role(Role.DEV).name("User One").isDeleted(false).build();
        memberRepository.save(user1);
        Member user2 = Member.builder().signId("user2").password("password").role(Role.DEV).name("User Two").isDeleted(false).build();
        memberRepository.save(user2);
        Member pl = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(pl);

        Project project = Project.builder().name("Test Project").leaderId(pl.getId()).isDeleted(false).build();
        projectRepository.save(project);

        ProjectMember pm1 = ProjectMember.builder().project(project).member(user1).isDeleted(false).build();
        projectMemberRepository.save(pm1);
        ProjectMember pm2 = ProjectMember.builder().project(project).member(user2).isDeleted(false).build();
        projectMemberRepository.save(pm2);
        ProjectMember projectLeader = ProjectMember.builder().project(project).member(pl).isDeleted(false).build();
        projectMemberRepository.save(projectLeader);

        // When
        List<MemberResponseDto> responseDtos = memberService.findMembersByRole(admin.getId(), project.getId(), Role.DEV);

        // Then
        assertThat(responseDtos).hasSize(2); // Only user1 should be returned
        assertThat(responseDtos).extracting("signId").containsExactly("user1", "user2");
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 특정 프로젝트의 특정 역할을 가진 회원 목록을 조회하려고 할 때 예외가 발생하는가")
    @Transactional
    void testFindMembersByRole_AsNonAdmin_ShouldThrowBadRequestException() {
        // Given
        Member nonAdmin = Member.builder().signId("user").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(nonAdmin);
        Member user1 = Member.builder().signId("user1").password("password").role(Role.DEV).name("User One").isDeleted(false).build();
        memberRepository.save(user1);
        Member user2 = Member.builder().signId("user2").password("password").role(Role.DEV).name("User Two").isDeleted(false).build();
        memberRepository.save(user2);
        Member pl = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(pl);

        Project project = Project.builder().name("Test Project").leaderId(pl.getId()).isDeleted(false).build();
        projectRepository.save(project);

        ProjectMember non = ProjectMember.builder().project(project).member(nonAdmin).isDeleted(false).build();
        projectMemberRepository.save(non);
        ProjectMember pm1 = ProjectMember.builder().project(project).member(user1).isDeleted(false).build();
        projectMemberRepository.save(pm1);
        ProjectMember pm2 = ProjectMember.builder().project(project).member(user2).isDeleted(false).build();
        projectMemberRepository.save(pm2);
        ProjectMember projectLeader = ProjectMember.builder().project(project).member(pl).isDeleted(false).build();
        projectMemberRepository.save(projectLeader);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.findMembersByRole(nonAdmin.getId(), project.getId(), Role.DEV));
        assertThat(exception.getMessage()).isEqualTo("관리자나 프로젝트 리더가 아닙니다.");
    }

    // Test for deleteMember
    @Test
    @DisplayName("관리자가 특정 회원을 삭제할 수 있는가")
    @Transactional
    void testDeleteMember_AsAdmin_ShouldDeleteMember() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member user = Member.builder().signId("user").password("password").role(Role.DEV).name("User").isDeleted(false).build();
        memberRepository.save(user);

        MemberDeleteRequestDto requestDto = new MemberDeleteRequestDto(user.getId());

        // When
        MemberResponseDto responseDto = memberService.deleteMember(admin.getId(), requestDto);

        // Then
        assertThat(responseDto.getSignId()).isEqualTo(user.getSignId());
        assertThat(responseDto.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 회원을 삭제하려고 할 때 예외가 발생하는가")
    @Transactional
    void testDeleteMember_AsNonAdmin_ShouldThrowBadRequestException() {
        // Given
        Member nonAdmin = Member.builder().signId("user").password("password").role(Role.DEV).name("Non-Admin User").isDeleted(false).build();
        memberRepository.save(nonAdmin);

        Member userToDelete = Member.builder().signId("userToDelete").password("password").role(Role.DEV).name("User To Delete").isDeleted(false).build();
        memberRepository.save(userToDelete);

        MemberDeleteRequestDto requestDto = new MemberDeleteRequestDto(userToDelete.getId());

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.deleteMember(nonAdmin.getId(), requestDto));
        assertThat(exception.getMessage()).isEqualTo("관리자가 아닙니다.");
    }

    @Test
    @DisplayName("관리자가 해결되지 않은 이슈가 있는 회원을 삭제하려고 할 때 예외가 발생하는가")
    @Transactional
    void testDeleteMember_WithUnresolvedIssues_ShouldThrowBadRequestException() {
        // Given
        Member admin = Member.builder().signId("admin").password("password").role(Role.ADMIN).name("Admin User").isDeleted(false).build();
        memberRepository.save(admin);

        Member user1 = Member.builder().signId("user1").password("password").role(Role.DEV).name("User One").isDeleted(false).build();
        memberRepository.save(user1);

        Member tester = Member.builder().signId("tester").password("password").role(Role.TESTER).name("Tester").isDeleted(false).build();
        memberRepository.save(tester);

        Member pl = Member.builder().signId("projectLeader").password("password").role(Role.PL).name("Project Leader").isDeleted(false).build();
        memberRepository.save(pl);

        Project project = Project.builder().name("Test Project").leaderId(pl.getId()).isDeleted(false).build();
        projectRepository.save(project);

        ProjectMember pm1 = ProjectMember.builder().project(project).member(user1).isDeleted(false).build();
        projectMemberRepository.save(pm1);

        ProjectMember pm2 = ProjectMember.builder().project(project).member(tester).isDeleted(false).build();
        projectMemberRepository.save(pm2);

        ProjectMember projectLeader = ProjectMember.builder().project(project).member(pl).isDeleted(false).build();
        projectMemberRepository.save(projectLeader);


        Issue unresolvedIssue = Issue.builder().reporter(tester).assignee(user1).description("이슈테스트").priority(Priority.MAJOR).title("테스트 이슈입니다.").status(Status.ASSIGNED).isDeleted(false).build();
        issueRepository.save(unresolvedIssue);

        MemberDeleteRequestDto requestDto = new MemberDeleteRequestDto(user1.getId());

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> memberService.deleteMember(admin.getId(), requestDto));
        assertThat(exception.getMessage()).isEqualTo("해당 사용자는 해결되지 않은 이슈에 할당되어 있어 역할을 변경할 수 없습니다.");
    }




}
