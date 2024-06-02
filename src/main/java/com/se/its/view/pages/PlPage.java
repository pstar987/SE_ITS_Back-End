package com.se.its.view.pages;

import com.se.its.domain.comment.presentation.SwingCommentController;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

public class PlPage extends JFrame {
    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private SwingCommentController swingCommentController;
    private Long userId;

    public PlPage(SwingMemberController swingMemberController, SwingProjectController swingProjectController,
                  SwingIssueController swingIssueController, SwingCommentController swingCommentController,
                  Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.swingCommentController = swingCommentController;
        this.userId = userId;

        initComponent();

        setTitle("CITS");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


    }

    private void initComponent() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("CITS : 이슈 관리 솔루션");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton projectManageBtn = new JButton("프로젝트 관리");
        add(projectManageBtn, gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton projectBrowseBtn = new JButton("프로젝트 조회");
        add(projectBrowseBtn, gbc);

        projectManageBtn.addActionListener(
                e -> new ProjectMangePage(swingMemberController, swingProjectController, userId).setVisible(true));
        projectBrowseBtn.addActionListener(
                e -> new ProjectBrowsePage(swingMemberController, swingProjectController, swingIssueController,
                        swingCommentController,
                        userId).setVisible(true)
        );

    }
}
