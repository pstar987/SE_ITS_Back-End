package com.se.its.domain.issue.presentation;

import com.se.its.domain.issue.application.IssueService;
import com.se.its.domain.issue.domain.IssueCategory;
import com.se.its.domain.issue.dto.request.IssueAssignRequestDto;
import com.se.its.domain.issue.dto.request.IssueCreateRequestDto;
import com.se.its.domain.issue.dto.request.IssueDeleteRequestDto;
import com.se.its.domain.issue.dto.request.IssueStatusUpdateRequestDto;
import com.se.its.domain.issue.dto.request.IssueUpdateRequestDto;
import com.se.its.domain.issue.dto.response.IssueRecommendResponseDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SwingIssueController {

    private final IssueService issueService;

    public IssueResponseDto create(Long id, IssueCreateRequestDto issueCreateRequestDto) {
        return issueService.createIssue(id, issueCreateRequestDto);
    }

    public IssueResponseDto getIssue(Long id, Long issueId) {
        return issueService.getIssue(id, issueId);
    }

    public List<IssueResponseDto> getIssues(Long id, Long projectId) {
        return issueService.getIssues(id, projectId);
    }

    public List<IssueResponseDto> getDevIssues(Long id, Long projectId) {
        return issueService.getDevIssues(id, projectId);
    }

    public List<IssueResponseDto> getTesterIssues(Long id, Long projectId) {
        return issueService.getTesterIssues(id, projectId);
    }

    public List<IssueResponseDto> getAllIssues(Long id) {
        return issueService.getAllIssues(id);
    }

    public IssueResponseDto assign(Long id, IssueAssignRequestDto issueAssignRequestDto) {
        return issueService.assignIssue(id, issueAssignRequestDto);
    }

    public IssueResponseDto removeRequest(Long id, IssueDeleteRequestDto issueDeleteRequestDto) {
        return issueService.removeRequest(id, issueDeleteRequestDto);
    }

    public List<IssueResponseDto> getRemoveRequestIssues(Long id) {
        return issueService.getRemoveRequestIssues(id);
    }

    public IssueResponseDto remove(Long id, IssueDeleteRequestDto issueDeleteRequestDto) {
        return issueService.removeIssue(id, issueDeleteRequestDto);
    }

    public IssueResponseDto update(Long id, IssueUpdateRequestDto issueUpdateRequestDto) {
        return issueService.updateIssue(id, issueUpdateRequestDto);
    }


    public IssueResponseDto updateDev(Long id, IssueStatusUpdateRequestDto issueStatusUpdateRequestDto) {
        return issueService.updateIssueDev(id, issueStatusUpdateRequestDto);
    }

    public IssueResponseDto reassign(Long id, IssueAssignRequestDto issueAssignRequestDto) {
        return issueService.reassignIssue(id, issueAssignRequestDto);
    }

    public List<IssueResponseDto> searchIssues(Long id, IssueCategory category, Long projectId, String keyword) {
        return issueService.searchIssues(id, category, projectId, keyword);
    }

    public List<IssueRecommendResponseDto> recommendIssues(Long id, Long issueId) {
        return issueService.recommendIssues(id, issueId);
    }
}