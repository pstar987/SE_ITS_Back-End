package com.se.its.view.pages;

import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.*;

public class IssuePage extends JFrame  {
    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private final Long userId;

    public IssuePage(SwingMemberController swingMemberController, SwingProjectController swingProjectController, SwingIssueController swingIssueController, Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.userId = userId;

        setTitle("이슈 상세정보");
        setSize(1000,800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(1,3));

        JPanel issueInfoPanel = new JPanel(new GridBagLayout());
        JPanel commentPanel = new JPanel(new GridBagLayout());

    }


}
