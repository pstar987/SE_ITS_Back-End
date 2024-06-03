package com.se.its.domain.member.application;

import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.Status;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.dto.request.*;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.ProjectMember;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.global.error.exceptions.BadRequestException;
import com.se.its.global.error.exceptions.UnauthorizedException;
import com.se.its.global.util.dto.DtoConverter;
import com.se.its.global.util.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.se.its.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final IssueRepository issueRepository;
    private final DtoConverter dtoConverter;
    private final EntityValidator entityValidator;

    @Transactional
    public MemberResponseDto signUp(Long signId, MemberSignUpRequestDto memberSignUpRequestDto) {
        Member admin = entityValidator.validateMember(signId);

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }
        if (isDuplicateSignId(memberSignUpRequestDto.getSignId())) {
            throw new BadRequestException(ROW_ALREADY_EXIST, "중복되는 ID가 존재합니다.");
        }
        if(memberSignUpRequestDto.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자를 생성할 수 없습니다.");
        }

        Member member = Member.builder()
                .signId(memberSignUpRequestDto.getSignId())
                .password(memberSignUpRequestDto.getPassword())
                .role(memberSignUpRequestDto.getRole())
                .name(memberSignUpRequestDto.getName())
                .isDeleted(false)
                .build();

        memberRepository.save(member);

        return dtoConverter.createMemberResponseDto(member);
    }

    @Transactional
    public MemberResponseDto adminCreate(MemberSignUpRequestDto memberSignUpRequestDto){
        if (isDuplicateSignId(memberSignUpRequestDto.getSignId())) {
            throw new BadRequestException(ROW_ALREADY_EXIST, "중복되는 ID가 존재합니다.");
        }

        Member admin = Member.builder()
                .signId(memberSignUpRequestDto.getSignId())
                .password(memberSignUpRequestDto.getPassword())
                .role(memberSignUpRequestDto.getRole())
                .name(memberSignUpRequestDto.getName())
                .isDeleted(false)
                .build();

        memberRepository.save(admin);

        return dtoConverter.createMemberResponseDto(admin);
    }

    @Transactional
    public MemberResponseDto signIn(MemberSignInRequestDto memberSignInRequestDto) {
        Member member = memberRepository.findBySignIdAndIsDeletedFalse(memberSignInRequestDto.getSignId())
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 사용자입니다."));

        if (!member.getPassword().equals(memberSignInRequestDto.getPassword())) {
            throw new UnauthorizedException(INVALID_SIGNIN, "유효하지 않은 비밀번호입니다.");
        }

        return dtoConverter.createMemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto findMemberById(Long signId) {
        Member member = entityValidator.validateMember(signId);

        return dtoConverter.createMemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findAllMembers(Long signId){
        Member admin = entityValidator.validateMember(signId);

        if(!entityValidator.isAdminOrPl(admin)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자나 프로젝트 리더가 아닙니다.");
        }

        List<Member> allMembers = memberRepository.findByIsDeletedIsFalse();

        return allMembers.stream()
                .filter(member -> !member.getRole().equals(Role.ADMIN))
                .map(dtoConverter::createMemberResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findMembersByAdminAndPL(Long signId, Long projectId){
        Member admin = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        entityValidator.isMemberOfProject(admin, project);

        if(!entityValidator.isAdminOrPl(admin)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자나 프로젝트 리더가 아닙니다.");
        }

        List<ProjectMember> allMembers = projectMemberRepository.findByProjectIdAndIsDeletedFalse(project.getId());

        return allMembers.stream()
                .map(pm -> dtoConverter.createMemberResponseDto(pm.getMember()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findMembersByRole(Long signId, Long projectId, Role role){
        Member admin = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(projectId);
        entityValidator.isMemberOfProject(admin, project);

        if(!entityValidator.isAdminOrPl(admin)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자나 프로젝트 리더가 아닙니다.");
        }

        List<ProjectMember> allMembers = projectMemberRepository.findByProjectIdAndIsDeletedFalse(project.getId());

        return allMembers.stream()
                .map(ProjectMember::getMember)
                .filter(member -> member.getRole().equals(role)) // 역할로 필터링
                .map(dtoConverter::createMemberResponseDto)
                .toList();
    }

    @Transactional
    public MemberResponseDto deleteMember(Long signId, MemberDeleteRequestDto memberDeleteRequestDto){
        Member admin = entityValidator.validateMember(signId);
        Member target = entityValidator.validateMember(memberDeleteRequestDto.getId());

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }

        if(target.getRole().equals(Role.DEV)){
            List<Issue> unresolvedIssues = issueRepository.findByAssigneeIdAndStatusNot(target.getId(), Status.RESOLVED);
            if (!unresolvedIssues.isEmpty()) {
                throw new BadRequestException(INVALID_REQUEST_ROLE, "해당 사용자는 해결되지 않은 이슈에 할당되어 있어 역할을 변경할 수 없습니다.");
            }
        }
        target.setIsDeleted(true);
        memberRepository.save(target);

        return dtoConverter.createMemberResponseDto(target);
    }


    @Transactional
    public MemberResponseDto updateMemberRole(Long signId, MemberRoleUpdateRequestDto memberRoleUpdateRequestDto){
        Member admin = entityValidator.validateMember(signId);
        Member target = entityValidator.validateMember(memberRoleUpdateRequestDto.getId());

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }

        if(memberRoleUpdateRequestDto.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자를 부여할 수 없습니다.");
        }

        if(target.getRole().equals(Role.DEV)){
            List<Issue> unresolvedIssues = issueRepository.findByAssigneeIdAndStatusNot(target.getId(), Status.RESOLVED);
            if (!unresolvedIssues.isEmpty()) {
                throw new BadRequestException(INVALID_REQUEST_ROLE, "해당 사용자는 해결되지 않은 이슈에 할당되어 있어 역할을 변경할 수 없습니다.");
            }
        }

        target.updateRole(memberRoleUpdateRequestDto.getRole());
        memberRepository.save(target);

        return dtoConverter.createMemberResponseDto(target);
    }

    private Boolean isDuplicateSignId(String signId){
        return memberRepository.findBySignIdAndIsDeletedFalse(signId).isPresent();
    }

}
