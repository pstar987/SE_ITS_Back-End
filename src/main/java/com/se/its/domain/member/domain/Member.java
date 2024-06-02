package com.se.its.domain.member.domain;

import com.se.its.domain.comment.domain.Comment;
import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.project.domain.ProjectMember;
import com.se.its.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "signId")
    private String signId;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projects;

    @OneToMany(mappedBy = "reporter")
    private List<Issue> reportedIssues;

    @OneToMany(mappedBy = "fixer")
    private List<Issue> fixedIssues;

    @OneToMany(mappedBy = "assignee")
    private List<Issue> assignedIssues;


    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public void setIsDeleted(Boolean isDeleted) {
        if(isDeleted != null) this.isDeleted = isDeleted;
    }

    public void updateRole(Role role){
        if(role != null) this.role = role;
    }
}
