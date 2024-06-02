package com.se.its.domain.issue.domain.repository;

import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.Status;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAll();

    List<Issue> findAllByIsDeletedFalse();
    List<Issue> findByReporterIdAndIsDeletedFalse(Long reporterId);

    List<Issue> findByProjectIdAndIsDeletedFalse(Long projectId);

    List<Issue> findByProjectIdAndReporterIdAndIsDeletedFalse(Long projectId, Long reporterId);

    List<Issue> findByProjectIdAndAssigneeIdAndIsDeletedFalse(Long projectId, Long assigneeId);

    Optional<Issue> findByIdAndIsDeletedFalse(Long id);

    List<Issue> findByStatusAndIsDeletedFalse(Status status);
}
