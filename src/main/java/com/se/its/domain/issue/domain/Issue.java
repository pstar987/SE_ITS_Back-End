package com.se.its.domain.issue.domain;


import com.se.its.domain.comment.domain.Comment;
import com.se.its.domain.member.domain.Member;
import com.se.its.domain.project.domain.Project;
import com.se.its.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Issue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporterId", nullable = false)
    private Member reporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fixerId")
    private Member fixer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigneeId")
    private Member assignee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projectId")
    private Project project;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Boolean isDeleted;

    private String category;


    public void setIsDeleted(Boolean isDeleted) {
        if (isDeleted != null) this.isDeleted = isDeleted;

    }

    public void setAssignee(Member assignee) {
        if (assignee != null) this.assignee = assignee;

    }

    public void setStatus(Status status) {
        if (status != null) this.status = status;
    }
    public void setPriority(Priority priority) {
        if (priority != null) this.priority = priority;
    }

    public void setDescription(String description) {
        if (description != null && !description.trim().isEmpty()) this.description = description;

    }

    public void setCategory(String category) {
        if (category != null && !category.trim().isEmpty()) this.category = category;
    }
}
