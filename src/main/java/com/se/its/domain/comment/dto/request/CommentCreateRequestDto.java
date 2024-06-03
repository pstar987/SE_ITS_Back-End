package com.se.its.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CommentCreateRequestDto {
    @NotNull(message = "이슈 ID를 입력해주세요.")
    private Long issueId;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
