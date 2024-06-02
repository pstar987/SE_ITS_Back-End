package com.se.its.view.pages;

import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.dto.request.IssueUpdateRequestDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
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

public class SetIssuePriorityPage extends JFrame {

    private SwingIssueController swingIssueController;
    private ProjectResponseDto currentProject;
    private final Long userId;
    private ProjectDetailPage parentPage;

    private List<IssueResponseDto> issueDtos;
    private JList<IssueResponseDto> issueDtoJList;


    public SetIssuePriorityPage(ProjectDetailPage parentPage, SwingIssueController swingIssueController,
                                ProjectResponseDto currentProject,
                                Long userId) {
        this.swingIssueController = swingIssueController;
        this.currentProject = currentProject;
        this.userId = userId;
        this.parentPage = parentPage;
        setTitle("이슈 우선순위 설정");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("이슈 우선순위 설정");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(new JLabel("이슈 목록"), gbc);

        issueDtoJList = new JList<>(new DefaultListModel<>());
        initIssueList(swingIssueController, currentProject, userId);
        issueDtoJList.setCellRenderer(new IssueListRender());

        issueDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = issueDtoJList.locationToIndex(e.getPoint());
                    IssueResponseDto selecetedIssue = issueDtoJList.getModel().getElementAt(index);
                    showIssuesDetailDialog(selecetedIssue, swingIssueController, userId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(issueDtoJList);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

    }

    private void showIssuesDetailDialog(IssueResponseDto currentIssue, SwingIssueController swingIssueController,
                                        Long userId) {
        JDialog dialog = new JDialog(this, "이슈 우선 순위 설정");
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel issueTitle = new JLabel(currentIssue.getTitle());
        issueTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        dialog.add(issueTitle, gbc);

        JLabel issueCategory = new JLabel(
                currentIssue.getCategory() == null ? "카테고리: 없음" : "카테고리: " + currentIssue.getCategory());
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(issueCategory, gbc);

        JLabel issueStatus = new JLabel("상태: " + currentIssue.getStatus().toString());
        gbc.gridy = 2;
        dialog.add(issueStatus, gbc);

        Priority[] priorityList = {Priority.BLOCKER, Priority.CRITICAL, Priority.MAJOR, Priority.MINOR,
                Priority.TRIVIAL};
        JComboBox<Priority> priorityComboBox = new JComboBox<>(priorityList);
        priorityComboBox.setSelectedItem(currentIssue.getPriority());

        gbc.gridy = 3;
        dialog.add(priorityComboBox, gbc);

        gbc.gridy = 4;
        dialog.add(new JLabel("설명"), gbc);

        JTextArea issueDescription = new JTextArea(currentIssue.getDescription());
        issueDescription.setLineWrap(true);
        issueDescription.setWrapStyleWord(true);
        issueDescription.setEditable(false);
        issueDescription.setPreferredSize(new Dimension(200,200));

        JScrollPane scrollPane = new JScrollPane(issueDescription);
        scrollPane.setPreferredSize(new Dimension(200,200));
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(scrollPane, gbc);

        JLabel issueReporter = new JLabel("보고자: " + currentIssue.getReporter().getName());
        gbc.gridy = 6;
        dialog.add(issueReporter, gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");
        JLabel issueReportedDate = new JLabel("보고 일시: " + currentIssue.getReportedDate().format(formatter));
        gbc.gridy = 7;
        dialog.add(issueReportedDate, gbc);

        JButton confirmBtn = new JButton("확인");
        gbc.gridy = 8;
        dialog.add(confirmBtn, gbc);

        confirmBtn.addActionListener(
                e -> {
                    changeIssuePriority((Priority) priorityComboBox.getSelectedItem(), currentIssue,
                            swingIssueController, userId);
                    parentPage.refreshIssues();
                    dialog.dispose();
                }
        );
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void changeIssuePriority(Priority priority, IssueResponseDto currentIssue,
                                     SwingIssueController swingIssueController, Long userId) {
        IssueUpdateRequestDto issueUpdateRequestDto =
                IssueUpdateRequestDto
                        .builder()
                        .issueId(currentIssue.getId())
                        .description(currentIssue.getDescription())
                        .status(currentIssue.getStatus())
                        .priority(priority)
                        .category(currentIssue.getCategory())
                        .build();
        swingIssueController.update(userId, issueUpdateRequestDto);
        refreshIssueList();
    }

    private class IssueListRender extends JPanel implements ListCellRenderer<IssueResponseDto> {
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
            issueName.setText("[" + value.getTitle() + "]");
            issueStatus.setText("상태: " + value.getStatus());
            issuePriority.setText("우선순위: " + value.getPriority().toString());
            issueAssignee.setText("담당자: " + (value.getAssignee() == null ? "지정 안됨" : value.getAssignee().getName()));
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

    private void refreshIssueList() {
        DefaultListModel<IssueResponseDto> listModel = (DefaultListModel<IssueResponseDto>) issueDtoJList.getModel();
        listModel.clear();
        issueDtos = swingIssueController.getIssues(userId, currentProject.getId());
        for (IssueResponseDto issueResponseDto : issueDtos) {
            listModel.addElement(issueResponseDto);
        }
        issueDtoJList.repaint();
        issueDtoJList.revalidate();
    }


    void initIssueList(SwingIssueController swingIssueController, ProjectResponseDto currentProject, Long userId) {
        issueDtos = swingIssueController.getIssues(userId, currentProject.getId());
        DefaultListModel<IssueResponseDto> listModel = (DefaultListModel<IssueResponseDto>) issueDtoJList.getModel();
        listModel.clear();

        for (IssueResponseDto issueResponseDto : issueDtos) {
            listModel.addElement(issueResponseDto);
        }
    }
}
