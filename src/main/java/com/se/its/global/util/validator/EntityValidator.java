package com.se.its.global.util.validator;

import com.se.its.domain.comment.domain.Comment;
import com.se.its.domain.comment.domain.repository.CommentRepository;
import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.repository.IssueRepository;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.domain.respository.MemberRepository;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.repository.ProjectMemberRepository;
import com.se.its.domain.project.domain.repository.ProjectRepository;
import com.se.its.global.error.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.se.its.global.error.ErrorCode.ROW_DOES_NOT_EXIST;

@Component
@RequiredArgsConstructor
public class EntityValidator {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public Member validateMember(Long memberId) {
        return memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 사용자입니다."));
    }
    public Project validateProject(Long projectId) {
        return projectRepository.findByIdAndIsDeletedIsFalse(projectId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 프로젝트입니다."));
    }
    public Issue validateIssue(Long issueId) {
        return issueRepository.findByIdAndIsDeletedFalse(issueId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 이슈입니다."));
    }

    public Issue validateRecommendIssue(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 이슈입니다."));
    }

    public Comment validateComment(Long issueId) {
        return commentRepository.findByIdAndIsDeletedFalse(issueId)
                .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "존재하지 않는 댓글입니다."));
    }
    public void isMemberOfProject(Member member, Project project) {
        if(!member.getRole().equals(Role.ADMIN)){
            projectMemberRepository.findByMemberIdAndProjectIdAndIsDeletedFalse(member.getId(), project.getId())
                    .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "해당 프로젝트의 멤버가 아닙니다."));
        }
    }
    public void isWriterOfComment(Member member, Comment comment) {
        if(!member.getRole().equals(Role.ADMIN)){
            commentRepository.findByWriterIdAndIdAndIsDeletedFalse(member.getId(), comment.getId())
                    .orElseThrow(() -> new BadRequestException(ROW_DOES_NOT_EXIST, "해당 댓글의 작성자가 아닙니다."));
        }
    }
    public Boolean isAdminOrPl(Member member){
        return (member.getRole().equals(Role.ADMIN) || member.getRole().equals(Role.PL));
    }

}
