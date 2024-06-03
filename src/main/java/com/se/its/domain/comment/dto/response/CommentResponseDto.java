package com.se.its.domain.comment.dto.response;


import com.se.its.domain.member.dto.response.MemberResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    private MemberResponseDto writer;
    private String content;
    private Boolean isDeleted;
}
