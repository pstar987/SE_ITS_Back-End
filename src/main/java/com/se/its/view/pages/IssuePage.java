package com.se.its.view.pages;

import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class IssuePage extends JFrame {
    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private ProjectResponseDto currentProject;
    private IssueResponseDto currentIssue;
    private final Long userId;

    public IssuePage(SwingMemberController swingMemberController, SwingProjectController swingProjectController,
                     SwingIssueController swingIssueController, ProjectResponseDto currentProject,
                     IssueResponseDto currentIssue, Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.currentProject = currentProject;
        this.currentIssue = currentIssue;
        this.userId = userId;

        setTitle("이슈 상세정보");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(1, 2));

        JPanel issueInfoPanel = initIssueInfo();
        JPanel commentPanel = new JPanel(new GridBagLayout());
        commentPanel.setBackground(Color.CYAN);

        add(issueInfoPanel);
        add(commentPanel);
    }

    private JPanel initIssueInfo() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel issueTitle = new JLabel(currentIssue.getTitle());
        issueTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        mainPanel.add(issueTitle, gbc);

        JLabel issueCategory = new JLabel(
                currentIssue.getCategory() == null ? "카테고리: 없음" : "카테고리: " + currentIssue.getCategory());
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(issueCategory, gbc);

        JLabel issueStatus = new JLabel("상태: " + currentIssue.getStatus().toString());
        gbc.gridy = 2;
        mainPanel.add(issueStatus, gbc);

        JLabel issuePriority = new JLabel("우선 순위: " + currentIssue.getPriority().toString());
        gbc.gridy = 3;
        mainPanel.add(issuePriority, gbc);

        gbc.gridy = 4;
        mainPanel.add(new JLabel("설명"), gbc);

        JTextArea issueDescription = new JTextArea(currentIssue.getDescription());
        issueDescription.setLineWrap(true);
        issueDescription.setWrapStyleWord(true);
        issueDescription.setEditable(false);
        issueDescription.setPreferredSize(new Dimension(200, 200));

        JScrollPane scrollPane = new JScrollPane(issueDescription);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(scrollPane, gbc);

        JLabel issueReporter = new JLabel("보고자: " + currentIssue.getReporter().getName());
        gbc.gridy = 6;
        mainPanel.add(issueReporter, gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");
        JLabel issueReportedDate = new JLabel("보고 일시: " + currentIssue.getReportedDate().format(formatter));
        gbc.gridy = 7;
        mainPanel.add(issueReportedDate, gbc);

        JLabel issueFixer = new JLabel("해결한 사람 : " + (currentIssue.getFixer() == null ? "없음" : currentIssue.getFixer().getName()));
        gbc.gridy =8;
        mainPanel.add(issueFixer, gbc);

        JLabel issueAssignee = new JLabel("담당자: " + (currentIssue.getAssignee() == null ? "없음" : currentIssue.getAssignee().getName()));
        gbc.gridy = 9;
        mainPanel.add(issueAssignee, gbc);

        return mainPanel;
    }


}
