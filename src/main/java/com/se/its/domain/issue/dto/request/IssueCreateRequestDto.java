package com.se.its.domain.issue.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueCreateRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "설명을 입력해주세요.")
    private String description;

    private Long projectId;

    private String category;

}
