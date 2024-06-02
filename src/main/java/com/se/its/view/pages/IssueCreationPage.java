package com.se.its.view.pages;

import com.se.its.domain.issue.dto.request.IssueCreateRequestDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

public class IssueCreationPage extends JFrame {
    private ProjectResponseDto currentProject;
    private SwingIssueController swingIssueController;
    private final Long userId;

    private JTextField issueTitle;
    private JTextArea issueDescription;
    private JTextField issueCategory;

    public IssueCreationPage(ProjectResponseDto currentProject, SwingIssueController swingIssueController,
                             Long userId) {
        this.currentProject = currentProject;
        this.swingIssueController = swingIssueController;
        this.userId = userId;

        setTitle("이슈 생성");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        initComponents();
    }

    void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("이슈 생성하기");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("이슈 설명:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        issueTitle = new JTextField(20);

        add(issueTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JLabel("이슈설명"), gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        issueDescription = new JTextArea(5, 20);
        issueDescription.setLineWrap(true);
        issueDescription.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(issueDescription);
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        add(new JLabel("이슈 카테고리"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        issueCategory = new JTextField(20);
        add(issueCategory, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton createIssueBtn = new JButton("생성하기");
        add(createIssueBtn, gbc);

        createIssueBtn.addActionListener(
                e -> {
                    createIssue(issueTitle.getText(), issueDescription.getText(), issueCategory.getText(),
                            currentProject);
                }
        );

    }



    private void createIssue(String issueTitle, String issueDescription, String issueCategory,
                             ProjectResponseDto currentProject) {
        IssueCreateRequestDto issueCreateRequestDto =
                IssueCreateRequestDto
                        .builder()
                        .title(issueTitle)
                        .description(issueDescription)
                        .projectId(currentProject.getId())
                        .category(issueCategory)
                        .build();

    }
}
