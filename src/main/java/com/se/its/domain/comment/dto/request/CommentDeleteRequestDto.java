package com.se.its.domain.comment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CommentDeleteRequestDto {
    @NotNull(message = "댓글이 지정되어야 합니다.")
    private Long commentId;
}
