package com.se.its.domain.comment.presentation;


import com.se.its.domain.comment.application.CommentService;
import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.comment.dto.response.CommentResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@Tag(name = "Comment", description = "Comment API")
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/create")
    public ResponseEntity<CommentResponseDto> createComment(
            @Valid @RequestHeader Long id,
            @Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto) {
        CommentResponseDto responseDto = commentService.createComment(id, commentCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
