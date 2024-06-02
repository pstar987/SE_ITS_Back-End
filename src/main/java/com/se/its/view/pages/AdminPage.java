package com.se.its.view.pages;

import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JLabel;

public class AdminPage extends JFrame {
    private JLabel message;
    private JPanel accountPanel;
    private JPanel projectPanel;
    private JButton accountCreateBtn;

    private JButton accountRuleChangeBtn;

    private JButton accountDeleteBtn;

    private JButton projectCreateBtn;
    private JButton projectManageBtn;
    private JButton projectDeleteBtn;

    private JButton projectBrowseBtn;
    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private final Long userId;

    public AdminPage(SwingMemberController swingMemberController, SwingProjectController swingProjectController, SwingIssueController swingIssueController,
                     Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.userId = userId;

        initComponents();

        setTitle("CITS");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void initComponents() {
        setLayout(new GridLayout(1, 2));

        accountPanel = new JPanel(new GridBagLayout());
        projectPanel = new JPanel(new GridBagLayout());

        GridBagConstraints accountGbc = new GridBagConstraints();

        accountGbc.insets = new Insets(10, 10, 10, 10);

        accountGbc.gridx = 0;
        accountGbc.gridy = 0;
        accountGbc.gridwidth = 2;
        accountGbc.anchor = GridBagConstraints.PAGE_START;

        JLabel titleLabel = new JLabel("CITS : 이슈 관리 솔루션");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        accountPanel.add(titleLabel, accountGbc);

        accountGbc.gridx = 1;
        accountGbc.gridy = 1;
        accountGbc.gridwidth = 2;
        accountGbc.anchor = GridBagConstraints.CENTER;
        accountCreateBtn = new JButton("계정 생성하기");
        accountPanel.add(accountCreateBtn, accountGbc);

        accountGbc.gridy = 2;
        accountRuleChangeBtn = new JButton("계정 직책 변경하기");
        accountPanel.add(accountRuleChangeBtn, accountGbc);

        accountGbc.gridy = 3;
        accountDeleteBtn = new JButton("계정 삭제하기");
        accountPanel.add(accountDeleteBtn, accountGbc);

        accountCreateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AccountCreationPage(swingMemberController, userId).setVisible(true);
            }
        });

        accountRuleChangeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AccountRuleChangePage(swingMemberController, userId).setVisible(true);
            }
        });

        accountDeleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AccountDeletePage(swingMemberController, userId).setVisible(true);
            }
        });

        projectPanel = new JPanel(new GridBagLayout());

        GridBagConstraints projectGbc = new GridBagConstraints();

        projectGbc.insets = new Insets(10, 10, 10, 10);

        projectGbc.gridx = 0;
        projectGbc.gridy = 0;
        projectGbc.gridwidth = 2;
        projectGbc.anchor = GridBagConstraints.PAGE_START;

        JLabel projectTitleLabel = new JLabel("프로젝트");
        projectTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        projectPanel.add(projectTitleLabel, projectGbc);

        projectGbc.gridwidth = 1;
        projectGbc.gridy = 1;
        projectGbc.anchor = GridBagConstraints.CENTER;
        projectCreateBtn = new JButton("프로젝트 생성");
        projectPanel.add(projectCreateBtn, projectGbc);

        projectGbc.gridy = 2;
        projectManageBtn = new JButton("프로젝트 관리");
        projectPanel.add(projectManageBtn, projectGbc);

        projectGbc.gridy = 3;
        projectDeleteBtn = new JButton("프로젝트 삭제");
        projectPanel.add(projectDeleteBtn, projectGbc);

        projectGbc.gridy = 4;
        projectBrowseBtn = new JButton("프로젝트 조회");
        projectPanel.add(projectBrowseBtn,projectGbc);

        projectCreateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProjectCreationPage(swingMemberController, swingProjectController, userId).setVisible(true);
            }
        });

        projectManageBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProjectMangePage(swingMemberController, swingProjectController, userId).setVisible(true);
            }
        });

        projectDeleteBtn.addActionListener(new ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {
                                                   new ProjectDeletePage(swingProjectController, userId).setVisible(true);
                                               }
                                           }
        );

        projectBrowseBtn.addActionListener(e -> new ProjectBrowsePage(swingMemberController,swingProjectController, swingIssueController, userId).setVisible(true));

        add(accountPanel);
        add(projectPanel);

    }
}
