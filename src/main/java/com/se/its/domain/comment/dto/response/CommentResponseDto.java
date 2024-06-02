package com.se.its.domain.comment.dto.response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    private Long writerId;
    private String content;
}
