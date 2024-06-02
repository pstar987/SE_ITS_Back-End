package com.se.its.domain.comment.presentation;

import com.se.its.domain.comment.application.CommentService;
import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.comment.dto.request.CommentDeleteRequestDto;
import com.se.its.domain.comment.dto.request.CommentUpdateRequestDto;
import com.se.its.domain.comment.dto.response.CommentResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
public class SwingCommentController {

    private final CommentService commentService;

    public CommentResponseDto createComment(
            Long id,
            CommentCreateRequestDto commentCreateRequestDto
    ) {
        return commentService.createComment(id, commentCreateRequestDto);
    }

    public List<CommentResponseDto> getComments(
            Long id,
            Long issueId
    ) {
        return commentService.getComments(id, issueId);
    }

    public CommentResponseDto updateComment(
            Long id,
            CommentUpdateRequestDto commentUpdateRequestDto
    ){
        return commentService.updateComment(id, commentUpdateRequestDto);
    }

    public String removeComment(
            Long id,
            CommentDeleteRequestDto commentDeleteRequestDto
    ){
        commentService.removeComment(id, commentDeleteRequestDto);
        return "삭제되었습니다.";
    }

}
