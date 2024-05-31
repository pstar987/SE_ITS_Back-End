package com.se.its.domain.member.presentation;

import com.se.its.domain.member.application.MemberService;
용import com.se.its.domain.member.dto.request.MemberDeleteRequestDto;
import com.se.its.domain.member.dto.request.MemberRoleUpdateRequestDto;
import com.se.its.domain.member.dto.request.MemberSignInRequestDto;
import com.se.its.domain.member.dto.request.MemberSignUpRequestDto;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "Member", description = "Member API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    public ResponseEntity<MemberResponseDto> signUp(
            @Valid @RequestHeader String signId,
            @Valid @RequestBody MemberSignUpRequestDto memberSignUpRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signUp(signId, memberSignUpRequestDto));
    }

    @PostMapping("/createAdmin")
    public ResponseEntity<MemberResponseDto> createAdmin(
            @Valid @RequestBody MemberSignUpRequestDto memberSignUpRequestDto
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.adminCreate(memberSignUpRequestDto));
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
