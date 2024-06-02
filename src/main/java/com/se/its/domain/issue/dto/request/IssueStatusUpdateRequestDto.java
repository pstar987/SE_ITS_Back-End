package com.se.its.domain.issue.dto.request;


import com.se.its.domain.issue.domain.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueStatusUpdateRequestDto {
    @NotNull(message = "이슈 ID를 입력해주세요.")
    private Long issueId;

    @NotNull(message = "상태를 입력해주세요.")
    private Status status;
}
