package com.se.its.domain.member.presentation;

import com.se.its.domain.member.application.MemberService;
import com.se.its.domain.member.dto.request.MemberDeleteRequestDto;
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

    @PostMapping("/signIn")
    public ResponseEntity<MemberResponseDto> signIn(
            @Valid @RequestBody MemberSignInRequestDto memberSignInRequestDto
    ) {
        return ResponseEntity.ok(memberService.signIn(memberSignInRequestDto));
    }

    @GetMapping("/find")
    public ResponseEntity<MemberResponseDto> findMemberById(
            @Valid @RequestHeader String signId
    ) {
        return ResponseEntity.ok(memberService.findMemberById(signId));
    }


    @GetMapping("/account")
    public ResponseEntity<List<MemberResponseDto>> findMembersByAdmin(
            @Valid @RequestHeader String signId
    ){
        return ResponseEntity.ok(memberService.findMembersByAdmin(signId));
    }


    @PutMapping("/account/delete")
    public ResponseEntity<MemberResponseDto> deleteMember(
            @Valid @RequestHeader String signId,
            @Valid @RequestBody MemberDeleteRequestDto memberDeleteRequestDto
    ){
        return ResponseEntity.ok(memberService.deleteMember(signId, memberDeleteRequestDto));
    }

    @PutMapping("/account/update")
    public ResponseEntity<MemberResponseDto> updateMember(
            @Valid @RequestHeader String signId,
            @Valid @RequestBody MemberRoleUpdateRequestDto memberRoleUpdateRequestDto
    ){
        return ResponseEntity.ok(memberService.updateMemberRole(signId, memberRoleUpdateRequestDto));
    }

}
