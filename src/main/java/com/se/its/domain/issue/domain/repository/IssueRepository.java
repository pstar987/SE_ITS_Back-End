package com.se.its.domain.issue.domain.repository;

import com.se.its.domain.issue.domain.Issue;
import org.aspectj.weaver.ast.Literal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAll();

    List<Issue> findAllByIsDeletedFalse();
    List<Issue> findByReporterIdAndIsDeletedFalse(Long reporterId);

    List<Issue> findByProjectIdAndIsDeletedFalse(Long projectId);

    List<Issue> findByProjectIdAndReporterIdAndIsDeletedFalse(Long projectId, Long reporterId);

    List<Issue> findByProjectIdAndAssigneeIdAndIsDeletedFalse(Long projectId, Long assigneeId);
}
