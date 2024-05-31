package com.se.its.domain.project.domain.repository;

import com.se.its.domain.member.domain.Member;
import com.se.its.domain.project.domain.Project;
import com.se.its.domain.project.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectId(Long ProjectId);

    boolean existsByMemberAndProject(Member member, Project project);

    List<ProjectMember> findByMember(Member member);
}
