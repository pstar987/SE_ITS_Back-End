package com.se.its.domain.comment.application;

import com.se.its.domain.comment.domain.Comment;
import com.se.its.domain.comment.domain.repository.CommentRepository;
import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.comment.dto.request.CommentDeleteRequestDto;
import com.se.its.domain.comment.dto.request.CommentUpdateRequestDto;
import com.se.its.domain.comment.dto.response.CommentResponseDto;
import com.se.its.global.util.dto.DtoConverter;
import com.se.its.global.util.validator.EntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ComponentScan(basePackages = {"com.se.its.global.util", "com.se.its.global.error"})
class CommentServiceTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Autowired
    private EntityValidator entityValidator;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, dtoConverter, entityValidator);
    }

    // createComment
    @Test
    @DisplayName("유효한 회원과 이슈로 댓글을 생성할 수 있는가")
    @Transactional
    void testCreateComment_ValidMemberAndIssue_ShouldCreateComment() {
        // Given
        Long memberId = 2L;
        Long issueId = 1L;
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto(issueId, "This is a comment");

        // When
        CommentResponseDto responseDto = commentService.createComment(memberId, requestDto);

        // Then
        assertThat(responseDto.getContent()).isEqualTo(requestDto.getContent());
    }

    @Test
    @DisplayName("유효하지 않은 회원으로 댓글을 생성하려고 할 때 예외가 발생하는가")
    @Transactional
    void testCreateComment_InvalidMember_ShouldThrowException() {
        // Given
        Long invalidMemberId = 999L;
        Long issueId = 1L;
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto(issueId, "This is a comment");

        // When & Then
        assertThrows(Exception.class, () -> commentService.createComment(invalidMemberId, requestDto));
    }

    // getComments
    @Test
    @DisplayName("유효한 회원과 이슈로 댓글 목록을 조회할 수 있는가")
    @Transactional
    void testGetComments_ValidMemberAndIssue_ShouldReturnComments() {
        // Given
        Long memberId = 2L;
        Long issueId = 1L;

        // When
        List<CommentResponseDto> responseDtos = commentService.getComments(memberId, issueId);

        // Then
        assertThat(responseDtos).isNotEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 이슈로 댓글 목록을 조회하려고 할 때 예외가 발생하는가")
    @Transactional
    void testGetComments_InvalidIssue_ShouldThrowException() {
        // Given
        Long memberId = 2L;
        Long invalidIssueId = 999L;

        // When & Then
        assertThrows(Exception.class, () -> commentService.getComments(memberId, invalidIssueId));
    }

    // updateComment
    @Test
    @DisplayName("유효한 회원과 댓글 ID로 댓글을 수정할 수 있는가")
    @Transactional
    void testUpdateComment_ValidMemberAndCommentId_ShouldUpdateComment() {
        // Given
        Long memberId = 2L;
        Long commentId = 1L;
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto(commentId, "Updated comment content");

        // When
        CommentResponseDto responseDto = commentService.updateComment(memberId, requestDto);

        // Then
        assertThat(responseDto.getContent()).isEqualTo(requestDto.getContent());
    }

    @Test
    @DisplayName("유효하지 않은 회원으로 댓글을 수정하려고 할 때 예외가 발생하는가")
    @Transactional
    void testUpdateComment_InvalidMember_ShouldThrowException() {
        // Given
        Long invalidMemberId = 999L;
        Long commentId = 1L;
        CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto(commentId, "Updated comment content");

        // When & Then
        assertThrows(Exception.class, () -> commentService.updateComment(invalidMemberId, requestDto));
    }

    // removeComment
    @Test
    @DisplayName("유효한 회원과 댓글 ID로 댓글을 삭제할 수 있는가")
    @Transactional
    void testRemoveComment_ValidMemberAndCommentId_ShouldRemoveComment() {
        // Given
        Long memberId = 2L;
        Long commentId = 1L;
        CommentDeleteRequestDto requestDto = new CommentDeleteRequestDto(commentId);

        // When
        commentService.removeComment(memberId, requestDto);

        // Then
        Comment deletedComment = commentRepository.findById(commentId).orElseThrow();
        assertThat(deletedComment.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 회원으로 댓글을 삭제하려고 할 때 예외가 발생하는가")
    @Transactional
    void testRemoveComment_InvalidMember_ShouldThrowException() {
        // Given
        Long invalidMemberId = 999L;
        Long commentId = 1L;
        CommentDeleteRequestDto requestDto = new CommentDeleteRequestDto(commentId);

        // When & Then
        assertThrows(Exception.class, () -> commentService.removeComment(invalidMemberId, requestDto));
    }
}
