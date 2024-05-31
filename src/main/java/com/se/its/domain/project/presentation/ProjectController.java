package com.se.its.domain.project.presentation;


import com.se.its.domain.project.application.ProjectService;
import com.se.its.domain.project.dto.request.ProjectCreateRequestDto;
import com.se.its.domain.project.dto.request.ProjectMemberAddRequestDto;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@Tag(name = "Project", description = "Project API")
public class ProjectController {
    private final ProjectService projectService;


    @PostMapping("/create")
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestHeader Long id,
            @RequestBody ProjectCreateRequestDto projectCreateRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(id, projectCreateRequestDto));
    }

    @GetMapping("")
    public ResponseEntity<List<ProjectResponseDto>> getAllProject(
            @Valid @RequestHeader Long id
    ) {
        return ResponseEntity.ok(projectService.getAllProject(id));
    }


    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProject(
            @Valid @RequestHeader Long id,
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectService.getProjectById(id, projectId));
    }

    @PutMapping("/{projectId}/member/add")
    public ResponseEntity<ProjectResponseDto> addMember(
            @Valid @RequestHeader Long id,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberAddRequestDto projectMemberAddRequestDto
    ){
        return ResponseEntity.ok(projectService.addMember(id, projectId, projectMemberAddRequestDto));

    }
    @PutMapping("/{projectId}/member/delete")
    public ResponseEntity<ProjectResponseDto> removeMember(
            @Valid @RequestHeader Long id,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberRemoveRequestDto projectMemberRemoveRequestDto
    ){
        return ResponseEntity.ok(projectService.removeMember(id, projectId, projectMemberRemoveRequestDto));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<String> removeProject(
            @Valid @RequestHeader Long id,
            @PathVariable Long projectId
    ){
        projectService.removeProject(id, projectId);
        return ResponseEntity.ok("removed");
    }




    /*
    * 프로젝트 생성  post : /api/v1/project/create
    * 프로젝트 조회  get  : /api/v1/project/{projectId}
    * 프로젝트 멤버 추가 put : /api/v1/project/{projectId}/member/add
    * 프로젝트 멤버 삭제 put : /api/v1/project/{projectId}/member/delete
    * 프로젝트 전체 조회 get : /api/v1/project
    * 프로젝트 삭제  put  : /api/v1/project/{projectId}
    *  */
}
