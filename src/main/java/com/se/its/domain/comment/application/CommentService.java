package com.se.its.domain.comment.application;

import com.se.its.domain.comment.domain.Comment;
import com.se.its.domain.comment.domain.repository.CommentRepository;
import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.comment.dto.request.CommentUpdateRequestDto;
import com.se.its.domain.comment.dto.response.CommentResponseDto;
import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.project.domain.Project;
import com.se.its.global.util.dto.DtoConverter;
import com.se.its.global.util.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final DtoConverter dtoConverter;
    private final EntityValidator entityValidator;

    @Transactional
    public CommentResponseDto createComment(Long signId, CommentCreateRequestDto commentCreateRequestDto) {
        Issue issue = entityValidator.validateIssue(commentCreateRequestDto.getIssueId());
        Member writer = entityValidator.validateMember(signId);
        Project project = entityValidator.validateProject(issue.getProject().getId());
        entityValidator.isMemberOfProject(writer, project);

        Comment comment = Comment.builder()
                .issue(issue)
                .writer(writer)
                .content(commentCreateRequestDto.getContent())
                .isDeleted(false)
                .build();
        Comment savedComment = commentRepository.save(comment);

        return dtoConverter.createCommentResponseDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long signId, Long issueId){
        Member member = entityValidator.validateMember(signId);
        Issue issue = entityValidator.validateIssue(issueId);
        Project project = entityValidator.validateProject(issue.getProject().getId());
        entityValidator.isMemberOfProject(member, project);


        return commentRepository.findByIssueIdAndIsDeletedFalse(issueId).stream()
                .map(dtoConverter::createCommentResponseDto)
                .toList();
    }

    @Transactional
    public CommentResponseDto updateComment(Long signId, CommentUpdateRequestDto commentUpdateRequestDto){
        Member writer = entityValidator.validateMember(signId);
        Comment comment = entityValidator.validateComment(commentUpdateRequestDto.getCommentId());
        entityValidator.isWriterOfComment(writer, comment);

        comment.setContent(commentUpdateRequestDto.getContent());
        commentRepository.save(comment);

        return dtoConverter.createCommentResponseDto(comment);
    }


}
