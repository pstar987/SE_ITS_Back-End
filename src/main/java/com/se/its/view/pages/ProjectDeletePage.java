package com.se.its.view.pages;

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

public class ProjectDeletePage extends JFrame {

    private SwingProjectController swingProjectController;
    private final Long userId;

    private List<ProjectResponseDto> projectResponseDtos;
    private JList<ProjectResponseDto> projectResponseDtoJList;

    public ProjectDeletePage(SwingProjectController swingProjectController, Long userId) {
        this.swingProjectController = swingProjectController;
        this.userId = userId;

        setTitle("프로젝트 삭제");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        initData();
        initComponent();
    }

    private void initData() {
        projectResponseDtos = swingProjectController.getAllProject(userId);
    }

    private void updateProjectList() {
        DefaultListModel<ProjectResponseDto> listModel = (DefaultListModel<ProjectResponseDto>) projectResponseDtoJList.getModel();
        listModel.clear();
        initData();
        for (ProjectResponseDto projectResponseDto : projectResponseDtos) {
            listModel.addElement(projectResponseDto);
        }
    }

    private void initComponent() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("프로젝트 삭제하기");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel subTitle = new JLabel("프로젝트 리스트");
        add(subTitle, gbc);

        projectResponseDtoJList = new JList<>(new DefaultListModel<>());
        updateProjectList();
        projectResponseDtoJList.setCellRenderer(new ProjectListRender());

        projectResponseDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = projectResponseDtoJList.locationToIndex(e.getPoint());
                    ProjectResponseDto selectedProject = projectResponseDtoJList.getModel().getElementAt(index);
                    showEdingDialog(selectedProject);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(projectResponseDtoJList);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);
    }

    private void showEdingDialog(ProjectResponseDto selectedProject) {
        JDialog dialog = new JDialog(this, "프로젝트 삭제", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("프로젝트 이름: " + selectedProject.getName());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(nameLabel, gbc);

        JButton deleteBtn = new JButton("삭제");
        deleteBtn.addActionListener(
                e -> {
                    removeProject(selectedProject);
                    JOptionPane.showMessageDialog(this, "삭제 완료");
                    dialog.dispose();
                }
        );
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(deleteBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void removeProject(ProjectResponseDto selectedProject) {
        swingProjectController.removeProject(userId, selectedProject.getId());
        updateProjectList();
    }

    class ProjectListRender extends JPanel implements ListCellRenderer<ProjectResponseDto> {

        private JLabel nameLabel;

        public ProjectListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            nameLabel = new JLabel();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(nameLabel, gbc);

        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ProjectResponseDto> list,
                                                      ProjectResponseDto value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            nameLabel.setText(value.getName());
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
