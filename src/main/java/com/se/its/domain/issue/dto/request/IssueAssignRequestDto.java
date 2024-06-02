package com.se.its.domain.issue.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueAssignRequestDto {
    @NotNull(message = "이슈 ID를 입력해주세요.")
    private Long issueId;

    @NotNull(message = "할당할 멤버 ID를 입력해주세요.")
    private Long assigneeId;
}
