package com.se.its.domain.comment.domain;

import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.member.domain.Member;
import com.se.its.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issueId", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "writerId", nullable = false)
    private Member writer;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isDeleted;

    public void setContent(String content) {
        if (content != null && !content.trim().isEmpty()) this.content = content;
    }

    public void setIsDeleted(Boolean isDeleted) {
        if(isDeleted != null) this.isDeleted = isDeleted;
    }
}
