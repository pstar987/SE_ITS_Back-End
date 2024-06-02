package com.se.its.domain.issue.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueRecommendRequestDto {
    private Long issue_id;
    private String title;
    private String description;
    private String category;
    private String priority;
}
