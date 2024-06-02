package com.se.its.domain.comment.domain.repository;

import com.se.its.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndIsDeletedFalse(Long id);

    List<Comment> findByIssueIdAndIsDeletedFalse(Long issueId);

    Optional<Comment> findByWriterIdAndIdAndIsDeletedFalse(Long writerId, Long Id);
}
