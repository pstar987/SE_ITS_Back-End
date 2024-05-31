package com.se.its.domain.project.presentation;


import com.se.its.domain.project.application.ProjectService;
import com.se.its.domain.project.dto.request.ProjectCreateRequestDto;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@Tag(name = "Project", description = "Project API")
public class ProjectController {
    private final ProjectService projectService;


    @PostMapping("/create")
    public ResponseEntity<ProjectResponseDto> createProject(
            @RequestHeader Long id,
            @RequestBody ProjectCreateRequestDto projectCreateRequestDto
    ) {
        ProjectResponseDto projectResponseDto = projectService.createProject(id, projectCreateRequestDto);
        return ResponseEntity.ok(projectResponseDto);
    }
}
