package com.se.its.domain.project.domain.repository;

import com.se.its.domain.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByIdAndIsDeletedIsFalse(Long id);
    List<Project> findByIsDeletedIsFalse();
}
