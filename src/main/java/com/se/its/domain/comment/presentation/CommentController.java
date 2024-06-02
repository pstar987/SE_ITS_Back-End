package com.se.its.domain.comment.presentation;


import com.se.its.domain.comment.application.CommentService;
import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.comment.dto.request.CommentUpdateRequestDto;
import com.se.its.domain.comment.dto.response.CommentResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@Tag(name = "Comment", description = "Comment API")
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/create")
    public ResponseEntity<CommentResponseDto> createComment(
            @Valid @RequestHeader Long id,
            @Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(id, commentCreateRequestDto));
    }

    @GetMapping("")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @Valid @RequestHeader Long id,
            @RequestParam Long issueId
    ) {
        return ResponseEntity.ok(commentService.getComments(id, issueId));
    }


    @PutMapping("/update")
    public ResponseEntity<CommentResponseDto> updateComment(
            @Valid @RequestHeader Long id,
            @Valid @RequestBody CommentUpdateRequestDto commentUpdateRequestDto
    ){
        return ResponseEntity.ok(commentService.updateComment(id, commentUpdateRequestDto));
    }

}
