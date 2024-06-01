package com.se.its.domain.issue.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class IssueCreateRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "제목을 입력해주세요.")
    private String description;


    private Long projectId;


}
