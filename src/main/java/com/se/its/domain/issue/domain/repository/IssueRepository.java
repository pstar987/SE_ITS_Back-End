package com.se.its.domain.issue.domain.repository;

import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.domain.Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAll();

    List<Issue> findAllByIsDeletedFalse();

    List<Issue> findByProjectIdAndIsDeletedFalse(Long projectId);

    Optional<Issue> findByIdAndIsDeletedFalse(Long id);

    List<Issue> findByStatusAndIsDeletedFalse(Status status);

    List<Issue> findByTitleContainingAndIsDeletedFalse(String title);

    List<Issue> findByPriorityAndIsDeletedFalse(Priority priority);

    @Query("SELECT i FROM Issue i WHERE i.isDeleted = false AND (i.assignee.name LIKE %?1% OR i.assignee.signId LIKE %?1%)")
    List<Issue> findByAssigneeNameOrSignIdAndIsDeletedFalse(String keyword);

}
