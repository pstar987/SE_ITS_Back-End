package com.se.its.view.pages;

import com.se.its.domain.comment.presentation.SwingCommentController;
import com.se.its.domain.issue.domain.IssueCategory;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

public class IssueSearchPage extends JFrame {

    private ProjectResponseDto currentProject;
    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private SwingCommentController swingCommentController;
    private final Long userId;
    private ProjectDetailPage parentPage;


    public IssueSearchPage(ProjectDetailPage parentPage,ProjectResponseDto currentProject, SwingMemberController swingMemberController,
                           SwingProjectController swingProjectController, SwingIssueController swingIssueController,
                           SwingCommentController swingCommentController, Long userId) {
        this.currentProject = currentProject;
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.swingCommentController = swingCommentController;
        this.userId = userId;
        this.parentPage = parentPage;

        setTitle("이슈 검색");
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
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("이슈 검색");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, gbc);

        IssueCategory[] issueCategories = {IssueCategory.TITLE, IssueCategory.STATUS, IssueCategory.PRIORITY,
                IssueCategory.ASSIGNEE};
        JComboBox<IssueCategory> issueCategoryJComboBox = new JComboBox<>(issueCategories);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(issueCategoryJComboBox, gbc);

        JTextField keywordTextField = new JTextField(20);
        gbc.gridx = 1;
        add(keywordTextField, gbc);

        JButton searchBtn = new JButton("검색");
        gbc.gridx = 2;
        add(searchBtn, gbc);

        ActionListener searchAction = e -> {
            List<IssueResponseDto> result = searchIssue(userId,
                    (IssueCategory) issueCategoryJComboBox.getSelectedItem(), currentProject,
                    keywordTextField.getText(), swingIssueController);
            new IssueSearchResultPage(parentPage,swingMemberController, swingProjectController, swingIssueController,
                    swingCommentController, currentProject, result, userId).setVisible(true);
        };

        searchBtn.addActionListener(searchAction);
        keywordTextField.addActionListener(searchAction);
    }

    private List<IssueResponseDto> searchIssue(Long userId, IssueCategory category, ProjectResponseDto currentProject,
                                               String keyword, SwingIssueController swingIssueController) {
        return swingIssueController.searchIssues(userId, category, currentProject.getId(), keyword);
    }


}
