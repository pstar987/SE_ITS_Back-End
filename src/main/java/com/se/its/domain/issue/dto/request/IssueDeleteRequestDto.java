package com.se.its.domain.issue.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class IssueDeleteRequestDto {
    @NotNull(message = "이슈 ID를 입력해주세요.")
    private Long issueId;
}
