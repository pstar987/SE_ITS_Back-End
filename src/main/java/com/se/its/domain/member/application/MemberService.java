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
import java.util.stream.Collectors;

import static com.se.its.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseDto signUp(String signId, MemberSignUpRequestDto memberSignUpRequestDto) {
        Member admin = getUser(signId);

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

        return MemberResponseDto.builder()
                .signId(member.getSignId())
                .role(member.getRole())
                .name(member.getName())
                .isDeleted(member.getIsDeleted())
                .build();
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

        return MemberResponseDto.builder()
                .signId(admin.getSignId())
                .role(admin.getRole())
                .name(admin.getName())
                .isDeleted(admin.getIsDeleted())
                .build();
    }

    @Transactional
    public MemberResponseDto signIn(MemberSignInRequestDto memberSignInRequestDto) {
        Member member = memberRepository.findByEmail(memberSignInRequestDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException(INVALID_SIGNIN, "유효하지 않은 이메일입니다."));
        if (!member.getPassword().equals(memberSignInRequestDto.getPassword())) {
            throw new UnauthorizedException(INVALID_SIGNIN, "유효하지 않은 비밀번호입니다.");
        }

        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .build();
    }

    public MemberResponseDto findMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "잘못된 Member ID 입니다."));
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .build();
    }

}
