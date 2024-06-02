package com.se.its.domain.issue.presentation;

import com.se.its.domain.issue.application.IssueService;
import com.se.its.domain.issue.dto.request.IssueAssignRequestDto;
import com.se.its.domain.issue.dto.request.IssueCreateRequestDto;
import com.se.its.domain.issue.dto.request.IssueDeleteRequestDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SwingIssueController {

    private final IssueService issueService;

    public IssueResponseDto create(
            Long id,
            IssueCreateRequestDto issueCreateRequestDto
    ) {
        return issueService.createIssue(id, issueCreateRequestDto);
    }

    public List<IssueResponseDto> getIssues(
            Long id,
            Long projectId
    ) {
        return issueService.getIssues(id, projectId);
    }

    public List<IssueResponseDto> getAllIssues(
            Long id
    ) {
        return issueService.getAllIssues(id);
    }

    public IssueResponseDto assign(
            Long id,
            IssueAssignRequestDto issueAssignRequestDto
    ) {
        return issueService.assignIssue(id, issueAssignRequestDto);
    }

    public IssueResponseDto remove(
            Long id,
            IssueDeleteRequestDto issueDeleteRequestDto
    ) {
        return issueService.removeRequest(id, issueDeleteRequestDto);
    }

    public IssueResponseDto reassign(
            Long id,
            IssueAssignRequestDto issueAssignRequestDto
    ) {
        return issueService.reassignIssue(id, issueAssignRequestDto);
    }

}
