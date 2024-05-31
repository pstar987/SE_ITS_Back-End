package com.se.its.domain.project.domain.repository;

import com.se.its.domain.member.domain.Member;
import com.se.its.domain.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
