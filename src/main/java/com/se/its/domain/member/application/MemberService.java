package com.se.its.domain.member.application;

import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.dto.request.*;
import com.se.its.domain.member.dto.response.MemberResponseDto;
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
    private final DtoConverter dtoConverter;
    private final EntityValidator entityValidator;

    @Transactional
    public MemberResponseDto signUp(Long id, MemberSignUpRequestDto memberSignUpRequestDto) {
        Member admin = entityValidator.validateMember(id);

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
    public MemberResponseDto findMemberById(Long id) {
        Member member = entityValidator.validateMember(id);

        return dtoConverter.createMemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findMembersByAdmin(Long id){
        Member admin = entityValidator.validateMember(id);

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }

        List<Member> allMembers = memberRepository.findByIsDeletedIsFalse();

        return allMembers.stream()
                .filter(member -> !member.getRole().equals(Role.ADMIN))
                .map(dtoConverter::createMemberResponseDto)
                .toList();
    }

    @Transactional
    public MemberResponseDto deleteMember(Long id, MemberDeleteRequestDto memberDeleteRequestDto){
        Member admin = entityValidator.validateMember(id);
        Member target = entityValidator.validateMember(memberDeleteRequestDto.getId());

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }

        // 5. isDeleted를 true로 변경
        target.setIsDeleted(true);

        return dtoConverter.createMemberResponseDto(target);
    }


    @Transactional
    public MemberResponseDto updateMemberRole(Long id, MemberRoleUpdateRequestDto memberRoleUpdateRequestDto){
        Member admin = entityValidator.validateMember(id);
        Member target = entityValidator.validateMember(memberRoleUpdateRequestDto.getId());

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }

        if(memberRoleUpdateRequestDto.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자를 부여할 수 없습니다.");
        }

        target.updateRole(memberRoleUpdateRequestDto.getRole());

        return dtoConverter.createMemberResponseDto(target);
    }

    private Boolean isDuplicateSignId(String signId){
        return memberRepository.findBySignIdAndIsDeletedFalse(signId).isPresent();
    }

}
