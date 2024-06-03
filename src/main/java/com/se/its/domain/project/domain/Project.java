package com.se.its.domain.project.domain;


//import com.se.its.domain.issue.domain.Issue;
import com.se.its.domain.issue.domain.Issue;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @Column(name = "leaderId")
    private Long leaderId;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projectMembers;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> Issues;

    public void setIsDeleted(Boolean isDeleted){
        if(isDeleted != null) this.isDeleted = isDeleted;
    }

    public void setLeaderId(Long leaderId){ this.leaderId = leaderId; }
}
