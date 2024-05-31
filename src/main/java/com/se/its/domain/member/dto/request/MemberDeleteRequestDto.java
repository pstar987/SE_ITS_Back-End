package com.se.its.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberDeleteRequestDto {
    @NotBlank(message = "올바른 id를 입력해주세요.")
    private String signId;
}
