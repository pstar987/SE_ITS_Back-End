package com.se.its.domain.issue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueUpdateRequestDto {
    @NotNull(message = "이슈 ID를 입력해주세요.")
    private Long issueId;

    @NotBlank(message = "업데이트할 설명을 입력해주세요.")
    private String description;
}
