package com.se.its.view.pages;

import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.*;

public class ProjectBrowsePage extends JFrame {
    private SwingProjectController swingProjectController;
    private Long userId;
    private List<ProjectResponseDto> projectDtos;
    private JList<ProjectResponseDto> projectDtoJList;

    public ProjectBrowsePage(SwingProjectController swingProjectController, Long userId) {
        this.swingProjectController = swingProjectController;
        this.userId = userId;

        setTitle("프로젝트 조회");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);


    }

    private void initData(){
        projectDtos = swingProjectController.getAllProject(userId);
        projectDtoJList = new JList<>(projectDtos.toArray(new ProjectResponseDto[]{projectDtos.get(0)}));
    }

    private void initComponent() {

    }


}
