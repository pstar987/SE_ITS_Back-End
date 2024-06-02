package com.se.its.domain.comment.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentUpdateRequestDto {
    @NotNull(message = "댓글이 지정되어야 합니다.")
    private Long commentId;

    private String content;
}