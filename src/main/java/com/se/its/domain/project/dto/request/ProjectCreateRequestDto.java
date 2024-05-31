package com.se.its.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProjectCreateRequestDto {

    @NotBlank(message = "프로젝트 이름을 입력해주세요.")
    private String name;

    private List<Long> memberIds;

}
