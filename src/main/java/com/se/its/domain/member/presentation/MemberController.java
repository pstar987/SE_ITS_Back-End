package com.se.its.domain.member.presentation;

import com.se.its.domain.member.application.MemberService;
import com.se.its.domain.member.dto.request.MemberSignInRequestDto;
import com.se.its.domain.member.dto.request.MemberSignUpRequestDto;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.global.payload.ApiResponseTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "Member", description = "Member API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponseTemplate<MemberResponseDto> signUp(
            @Valid @RequestBody MemberSignUpRequestDto memberSignUpRequestDto
    ) {
        return ApiResponseTemplate.created(memberService.signUp(memberSignUpRequestDto));
    }

    @PostMapping("/signin")
    public ApiResponseTemplate<MemberResponseDto> signIn(
            @Valid @RequestBody MemberSignInRequestDto memberSignInRequestDto
    ) {
        return ApiResponseTemplate.ok(memberService.signIn(memberSignInRequestDto));
    }

    @GetMapping("/find")
    public ApiResponseTemplate<MemberResponseDto> findMemberById(
            @Valid @RequestHeader Long memberId
    ) {
        return ApiResponseTemplate.ok(memberService.findMemberById(memberId));
    }

}
