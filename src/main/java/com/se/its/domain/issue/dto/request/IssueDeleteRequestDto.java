package com.se.its.domain.issue.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class IssueDeleteRequestDto {
    @NotNull(message = "이슈 ID를 입력해주세요.")
    private Long issueId;
}
