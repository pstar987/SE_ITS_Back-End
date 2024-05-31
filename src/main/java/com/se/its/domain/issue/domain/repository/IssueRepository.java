package com.se.its.domain.issue.domain.repository;

import com.se.its.domain.issue.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}
