package com.se.its.domain.issue.presentation;


import com.se.its.domain.issue.application.IssueService;
import com.se.its.domain.issue.dto.request.IssueCreateRequestDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.member.dto.request.MemberSignUpRequestDto;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/issue")
@Tag(name = "Issue", description = "Issue API")
public class IssueController {
    private final IssueService issueService;

    @PostMapping("/create")
    public ResponseEntity<IssueResponseDto> create(
            @Valid @RequestHeader Long id,
            @Valid @RequestBody IssueCreateRequestDto issueCreateRequestDto
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.createIssue(id, issueCreateRequestDto));
    }
}
