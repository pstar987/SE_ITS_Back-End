package com.se.its.view.pages;

import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class ProjectBrowsePage extends JFrame {
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private Long userId;
    private List<ProjectResponseDto> projectDtos;
    private JList<ProjectResponseDto> projectDtoJList;

    private List<IssueResponseDto> issueDtos;
    private JList<IssueResponseDto> issueDtoJList;

    public ProjectBrowsePage(SwingProjectController swingProjectController, SwingIssueController swingIssueController,
                             Long userId) {
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.userId = userId;

        setTitle("프로젝트 조회");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        initComponent();


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

    private void initComponent() {
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
                    //TODO 프로젝트 상세 정보 창 -> 프로젝트 명, 이슈 리스트들
                    showProjectDetail(selectedProject, swingIssueController, userId);
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

    private void initIssueData(ProjectResponseDto selectedProject) {
        issueDtos = swingIssueController.getIssues(userId, selectedProject.getId());
        DefaultListModel<IssueResponseDto> listModel = (DefaultListModel<IssueResponseDto>) issueDtoJList.getModel();
        listModel.clear();
        for (IssueResponseDto issueResponseDto : issueDtos) {
            listModel.addElement(issueResponseDto);
        }
    }

    private void showProjectDetail(ProjectResponseDto selectedProject, SwingIssueController swingIssueController,
                                   Long userId) {
        JDialog dialog = new JDialog(this, "프로젝트 상세 정보", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(300, 400);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel projectNameLabel = new JLabel(selectedProject.getName());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(projectNameLabel, gbc);

        JLabel issueListLabel = new JLabel("이슈 리스트");
        gbc.gridy = 1;
        gbc.gridwidth =2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(issueListLabel,gbc);

        issueDtoJList = new JList<>(new DefaultListModel<>());
        initIssueData(selectedProject);
        issueDtoJList.setCellRenderer(new IssueListRender());

        JScrollPane scrollPane = new JScrollPane(issueDtoJList);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dialog.add(scrollPane, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    class IssueListRender extends JPanel implements ListCellRenderer<IssueResponseDto> {
        private JLabel issueName;
        private JLabel issuePriority;
        private JLabel issueStatus;
        private JLabel issueAssignee;
        private JLabel issueReportedDate;
        private DateTimeFormatter formatter;
        public IssueListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            issueName = new JLabel();
            issuePriority = new JLabel();
            issueStatus = new JLabel();
            issueAssignee = new JLabel();
            issueReportedDate = new JLabel();
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");


            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(issueName, gbc);

            gbc.gridx = 1;
            add(issueStatus, gbc);

            gbc.gridx = 2;
            add(issuePriority, gbc);

            gbc.gridx = 3;
            add(issueAssignee, gbc);

            gbc.gridx = 4;
            add(issueReportedDate, gbc);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends IssueResponseDto> list, IssueResponseDto value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            issueName.setText("["+value.getTitle()+"]");
            issueStatus.setText("상태: " + value.getStatus());
            issuePriority.setText("우선순위: " + value.getPriority());
            issuePriority.setText("담당자: " + (value.getAssignee() == null ? "지정 안됨" : value.getAssignee().getName()));
            issueReportedDate.setText("일시: " + value.getReportedDate().format(formatter));
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
