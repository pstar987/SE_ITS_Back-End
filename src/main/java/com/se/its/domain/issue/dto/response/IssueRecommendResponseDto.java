package com.se.its.domain.issue.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueRecommendResponseDto {
    private IssueResponseDto issueResponseDto;
    private Long score;
    private Boolean isDeleted;
}
