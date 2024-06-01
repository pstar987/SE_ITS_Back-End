package com.se.its.domain.member.application;

import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.dto.request.*;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.global.error.exceptions.BadRequestException;
import com.se.its.global.error.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.se.its.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseDto signUp(Long id, MemberSignUpRequestDto memberSignUpRequestDto) {
        Member admin = getUser(id);

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

        return createMemberResponseDto(member);
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

        return createMemberResponseDto(admin);
    }

    @Transactional
    public MemberResponseDto signIn(MemberSignInRequestDto memberSignInRequestDto) {
        Member member = memberRepository.findBySignIdAndIsDeletedFalse(memberSignInRequestDto.getSignId())
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 사용자입니다."));

        if (!member.getPassword().equals(memberSignInRequestDto.getPassword())) {
            throw new UnauthorizedException(INVALID_SIGNIN, "유효하지 않은 비밀번호입니다.");
        }

        return createMemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto findMemberById(Long id) {
        Member member = getUser(id);

        return createMemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> findMembersByAdmin(Long id){
        Member admin = getUser(id);

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }

        List<Member> allMembers = memberRepository.findByIsDeletedIsFalse();

        return allMembers.stream()
                .filter(member -> !member.getRole().equals(Role.ADMIN))
                .map(this::createMemberResponseDto)
                .toList();
    }

    @Transactional
    public MemberResponseDto deleteMember(Long id, MemberDeleteRequestDto memberDeleteRequestDto){
        Member admin = getUser(id);
        Member target = getUser(memberDeleteRequestDto.getId());

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }

        // 5. isDeleted를 true로 변경
        target.setIsDeleted(true);

        return createMemberResponseDto(target);
    }


    @Transactional
    public MemberResponseDto updateMemberRole(Long id, MemberRoleUpdateRequestDto memberRoleUpdateRequestDto){
        Member admin = getUser(id);
        Member target = getUser(memberRoleUpdateRequestDto.getId());

        if(!admin.getRole().equals(Role.ADMIN)){
            throw  new BadRequestException(INVALID_REQUEST_ROLE, "관리자가 아닙니다.");
        }

        if(memberRoleUpdateRequestDto.getRole().equals(Role.ADMIN)){
            throw new BadRequestException(INVALID_REQUEST_ROLE, "관리자를 부여할 수 없습니다.");
        }

        target.updateRole(memberRoleUpdateRequestDto.getRole());

        return createMemberResponseDto(target);
    }

    private MemberResponseDto createMemberResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .role(member.getRole())
                .isDeleted(member.getIsDeleted())
                .build();
    }

    private Member getUser(Long targetId){
        return memberRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 사용자입니다."));
    }

    private Boolean isDuplicateSignId(String signId){
        return memberRepository.findBySignIdAndIsDeletedFalse(signId).isPresent();
    }

}
