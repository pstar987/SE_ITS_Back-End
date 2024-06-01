package com.se.its.domain.project.presentation;

import com.se.its.domain.project.application.ProjectService;
import com.se.its.domain.project.dto.request.ProjectCreateRequestDto;
import com.se.its.domain.project.dto.request.ProjectMemberAddRequestDto;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SwingProjectController {

    private final ProjectService projectService;

    public ProjectResponseDto createProject(
            Long id,
            ProjectCreateRequestDto projectCreateRequestDto
    ) {
        return projectService.createProject(id, projectCreateRequestDto);
    }

    public List<ProjectResponseDto> getAllProject(
            Long id
    ) {
        return projectService.getAllProject(id);
    }

    public ProjectResponseDto getProject(
            Long id,
            Long projectId
    ) {
        return projectService.getProjectById(id, projectId);
    }

    public ProjectResponseDto addMember(
            Long id,
            Long projectId,
            ProjectMemberAddRequestDto projectMemberAddRequestDto
    ){
        return projectService.addMember(id, projectId, projectMemberAddRequestDto);
    }

}
