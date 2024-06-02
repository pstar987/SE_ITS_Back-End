package com.se.its.view.pages;

import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;

public class ProjectBrowsePage extends JFrame {
    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private Long userId;
    private List<ProjectResponseDto> projectDtos;
    private JList<ProjectResponseDto> projectDtoJList;


    public ProjectBrowsePage(SwingMemberController swingMemberController, SwingProjectController swingProjectController,
                             SwingIssueController swingIssueController,
                             Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.userId = userId;

        setTitle("프로젝트 조회");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        initDefaultComponent();


    }

    private void initData() {
        projectDtos = swingProjectController.getAllProject(userId);
    }

    private void updateProjectList() {
        initData();
        DefaultListModel<ProjectResponseDto> listModel = (DefaultListModel<ProjectResponseDto>) projectDtoJList.getModel();
        listModel.clear();
        for (ProjectResponseDto projectResponseDto : projectDtos) {
            listModel.addElement(projectResponseDto);
        }
    }

    private void initDefaultComponent() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("프로젝트 조회");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel subTitle = new JLabel("프로젝트 리스트");
        add(subTitle, gbc);

        projectDtoJList = new JList<>(new DefaultListModel<>());
        updateProjectList();
        projectDtoJList.setCellRenderer(new ProjectListRender());

        projectDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = projectDtoJList.locationToIndex(e.getPoint());
                    ProjectResponseDto selectedProject = projectDtoJList.getModel().getElementAt(index);
                    new ProjectDetailPage(selectedProject, swingMemberController, swingProjectController, swingIssueController,userId).setVisible(true);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(projectDtoJList);
        scrollPane.setPreferredSize(new Dimension(200, 300));

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);
    }

    class ProjectListRender extends JPanel implements ListCellRenderer<ProjectResponseDto> {
        private JLabel projectNameLabel;

        public ProjectListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            projectNameLabel = new JLabel();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(projectNameLabel, gbc);

        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ProjectResponseDto> list,
                                                      ProjectResponseDto value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            projectNameLabel.setText(value.getName());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }

}
