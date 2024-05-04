package com.se.its.domain.member.application;

import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.member.dto.request.MemberSignInRequestDto;
import com.se.its.domain.member.dto.request.MemberSignUpRequestDto;
import com.se.its.domain.member.dto.request.MemberUpdateRequestDto;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.store.domain.Store;
import com.se.its.global.error.exceptions.BadRequestException;
import com.se.its.global.error.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.se.its.global.error.ErrorCode.INVALID_SIGNIN;
import static com.se.its.global.error.ErrorCode.ROW_DOES_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseDto signUp(MemberSignUpRequestDto memberSignUpRequestDto) {
        Store store = Store.builder().build();

        Member member = Member.builder()
                .email(memberSignUpRequestDto.getEmail())
                .password(memberSignUpRequestDto.getPassword())
                .age(memberSignUpRequestDto.getAge())
                .store(store)
                .build();

        memberRepository.save(member);

        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .age(member.getAge())
                .level(member.getLevel())
                .build();
    }

    public MemberResponseDto signIn(MemberSignInRequestDto memberSignInRequestDto) {
        Member member = memberRepository.findByEmail(memberSignInRequestDto.getEmail())
                .orElseThrow(() -> new UnauthorizedException(INVALID_SIGNIN, "유효하지 않은 이메일입니다."));
        if (!member.getPassword().equals(memberSignInRequestDto.getPassword())) {
            throw new UnauthorizedException(INVALID_SIGNIN, "유효하지 않은 비밀번호입니다.");
        }

        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .age(member.getAge())
                .level(member.getLevel())
                .build();
    }

    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto memberUpdateRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "잘못된 Member ID 입니다."));

        member.updateMember(memberUpdateRequestDto);
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .age(member.getAge())
                .level(member.getLevel())
                .build();
    }

    public MemberResponseDto findMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "잘못된 Member ID 입니다."));
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .age(member.getAge())
                .level(member.getLevel())
                .build();
    }

}
