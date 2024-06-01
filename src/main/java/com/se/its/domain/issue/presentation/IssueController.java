package com.se.its.domain.issue.presentation;


import com.se.its.domain.issue.application.IssueService;
import com.se.its.domain.issue.dto.request.IssueAssignRequestDto;
import com.se.its.domain.issue.dto.request.IssueCreateRequestDto;
import com.se.its.domain.issue.dto.request.IssueDeleteRequestDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("{projectId}")
    public ResponseEntity<List<IssueResponseDto>> getIssues(
            @Valid @RequestHeader Long id,
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(issueService.getIssues(id ,projectId));
    }

    @GetMapping("")
    public ResponseEntity<List<IssueResponseDto>> getAllIssues(
            @Valid @RequestHeader Long id
    ){
        return ResponseEntity.ok(issueService.getAllIssues(id));
    }

    @PutMapping("/assign")
    public ResponseEntity<IssueResponseDto> assign(
            @Valid @RequestHeader Long id,
            @Valid @RequestBody IssueAssignRequestDto issueAssignRequestDto
    ) {
        return ResponseEntity.ok(issueService.assignIssue(id ,issueAssignRequestDto));
    }

    @PutMapping("/deleteRequest")
    public ResponseEntity<IssueResponseDto> removeRequest(
            @Valid @RequestHeader Long id,
            @Valid @RequestBody IssueDeleteRequestDto issueDeleteRequestDto
    ) {
        return ResponseEntity.ok(issueService.removeRequest(id ,issueDeleteRequestDto));
    }
}
