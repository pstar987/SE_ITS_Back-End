package com.se.its.view.pages;

import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.comment.dto.response.CommentResponseDto;
import com.se.its.domain.comment.presentation.SwingCommentController;
import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class IssuePage extends JFrame {
    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private SwingCommentController swingCommentController;
    private ProjectResponseDto currentProject;
    private IssueResponseDto currentIssue;
    private final Long userId;

    private DefaultListModel<CommentResponseDto> commentListModel;
    private JList<CommentResponseDto> commentList;

    public IssuePage(SwingMemberController swingMemberController, SwingProjectController swingProjectController,
                     SwingIssueController swingIssueController, SwingCommentController swingCommentController,
                     ProjectResponseDto currentProject,
                     IssueResponseDto currentIssue, Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.swingCommentController = swingCommentController;
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
        JPanel commentPanel = initCommentPanel();

        add(issueInfoPanel);
        add(commentPanel);
    }


    private JPanel initCommentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        commentListModel = new DefaultListModel<>();
        commentList = new JList<>(commentListModel);
        commentList.setCellRenderer(new CommentListRenderer());
        JScrollPane commentScrollPane = new JScrollPane(commentList);

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextArea commentTextArea = new JTextArea(3, 20);
        commentTextArea.setLineWrap(true);
        commentTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(commentTextArea);

        JButton addCommentButton = new JButton("코멘트 추가");
        addCommentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = commentTextArea.getText().trim();
                if (!content.isEmpty()) {
                    CommentCreateRequestDto requestDto = CommentCreateRequestDto.builder()
                            .issueId(currentIssue.getId())
                            .content(content)
                            .build();
                    swingCommentController.createComment(userId, requestDto);
                    commentTextArea.setText("");
                    loadComments();
                }
            }
        });

        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        inputPanel.add(addCommentButton, BorderLayout.EAST);

        mainPanel.add(new JLabel("Comments"), BorderLayout.NORTH);
        mainPanel.add(commentScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        loadComments();

        return mainPanel;
    }

    private void loadComments() {
        List<CommentResponseDto> comments = swingCommentController.getComments(userId, currentIssue.getId());
        commentListModel.clear();
        for (CommentResponseDto comment : comments) {
            commentListModel.addElement(comment);
        }
    }

    //TODO 추가적인 위젯 구현 필요
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

        JLabel issueFixer = new JLabel(
                "해결한 사람 : " + (currentIssue.getFixer() == null ? "없음" : currentIssue.getFixer().getName()));
        gbc.gridy = 8;
        mainPanel.add(issueFixer, gbc);

        JLabel issueAssignee = new JLabel(
                "담당자: " + (currentIssue.getAssignee() == null ? "없음" : currentIssue.getAssignee().getName()));
        gbc.gridy = 9;
        mainPanel.add(issueAssignee, gbc);

        return mainPanel;
    }

    private class CommentListRenderer extends JPanel implements ListCellRenderer<CommentResponseDto> {
        private JLabel writerLabel;
        private JTextArea contentArea;

        public CommentListRenderer() {
            setLayout(new BorderLayout());
            writerLabel = new JLabel();
            contentArea = new JTextArea();
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            contentArea.setEditable(false);
            add(writerLabel, BorderLayout.NORTH);
            add(new JScrollPane(contentArea), BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends CommentResponseDto> list,
                                                      CommentResponseDto value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            MemberResponseDto member = swingMemberController.findMemberById(value.getWriterId());
            writerLabel.setText(member.getName());
            contentArea.setText(value.getContent());
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
