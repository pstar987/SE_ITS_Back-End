package com.se.its.view.pages;

import com.se.its.domain.comment.dto.request.CommentCreateRequestDto;
import com.se.its.domain.comment.dto.request.CommentUpdateRequestDto;
import com.se.its.domain.comment.dto.response.CommentResponseDto;
import com.se.its.domain.comment.presentation.SwingCommentController;
import com.se.its.domain.issue.domain.Priority;
import com.se.its.domain.issue.domain.Status;
import com.se.its.domain.issue.dto.request.IssueAssignRequestDto;
import com.se.its.domain.issue.dto.request.IssueDeleteRequestDto;
import com.se.its.domain.issue.dto.request.IssueStatusUpdateRequestDto;
import com.se.its.domain.issue.dto.request.IssueUpdateRequestDto;
import com.se.its.domain.issue.dto.response.IssueRecommendResponseDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private ProjectDetailPage parentPage;
    private final Long userId;

    private DefaultListModel<CommentResponseDto> commentListModel;
    private JList<CommentResponseDto> commentList;

    private List<MemberResponseDto> devDto;
    private JList<MemberResponseDto> devDtoJList;

    private List<IssueRecommendResponseDto> issueRecommendDtos;
    private JList<IssueRecommendResponseDto> issueRecommendDtoJList;


    private JPanel issueInfoPanel;
    private JPanel commentPanel;

    public IssuePage(ProjectDetailPage parentPage, SwingMemberController swingMemberController,
                     SwingProjectController swingProjectController,
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
        this.parentPage = parentPage;

        setTitle("이슈 상세정보");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(1, 2));

        issueInfoPanel = initIssueInfo();
        commentPanel = initCommentPanel();

        add(issueInfoPanel);
        add(commentPanel);
    }


    private JPanel initCommentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        commentListModel = new DefaultListModel<>();
        commentList = new JList<>(commentListModel);
        commentList.setCellRenderer(new CommentListRenderer());

        commentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = commentList.locationToIndex(e.getPoint());
                    CommentResponseDto selectedComment = commentList.getModel().getElementAt(index);
                    showCommentEditingDialog(swingMemberController, swingCommentController, selectedComment, userId);
                }
            }
        });

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

    private void showCommentEditingDialog(SwingMemberController swingMemberController,
                                          SwingCommentController swingCommentController,
                                          CommentResponseDto selectedComment, Long userId) {
        JDialog dialog = new JDialog(this, "코멘트 편집", true);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        MemberResponseDto memberResponseDto = swingMemberController.findMemberById(selectedComment.getWriter().getId());
        JLabel writer = new JLabel("작성자: " + memberResponseDto.getName());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(writer, gbc);

        gbc.gridy = 1;
        JLabel beforeEdit = new JLabel("수정 전: " + selectedComment.getContent());
        dialog.add(beforeEdit, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(new JLabel("수정할 내용: "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        JTextArea edittedContent = new JTextArea();
        edittedContent.setLineWrap(true);
        edittedContent.setWrapStyleWord(true);
        edittedContent.setPreferredSize(new Dimension(200, 200));

        JScrollPane scrollPane = new JScrollPane(edittedContent);
        scrollPane.setPreferredSize(new Dimension(200, 200));

        dialog.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton editBtn = new JButton("수정");

        editBtn.addActionListener(
                e -> {
                    CommentUpdateRequestDto commentUpdateRequestDto =
                            CommentUpdateRequestDto
                                    .builder()
                                    .commentId(selectedComment.getId())
                                    .content(edittedContent.getText())
                                    .build();
                    try {
                        swingCommentController.updateComment(userId, commentUpdateRequestDto);
                        JOptionPane.showMessageDialog(this, "수정 완료", "알림", JOptionPane.INFORMATION_MESSAGE);
                        loadComments();
                        dialog.dispose();
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(this, exception.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
                        dialog.dispose();
                    }
                }
        );

        dialog.add(editBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);


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

        gbc.gridy = 10;
        JButton issueRecommendBtn = new JButton("이슈 추천 받기");
        mainPanel.add(issueRecommendBtn, gbc);

        issueRecommendBtn.addActionListener(
                e -> {
                    showIssueRecommendDialog(swingIssueController, currentIssue, userId);
                }
        );

        gbc.gridy = 11;
        JButton issueEditBtn = new JButton("이슈 수정하기");
        mainPanel.add(issueEditBtn, gbc);

        MemberResponseDto memberResponseDto = swingMemberController.findMemberById(userId);
        issueEditBtn.addActionListener(
                e -> {
                    if (memberResponseDto.getRole().toString().equals("ADMIN") || memberResponseDto.getRole().toString()
                            .equals("PL")) {
                        showAdminPlEditDialog(currentIssue, swingMemberController, swingIssueController, userId);
                    } else if (memberResponseDto.getRole().toString().equals("DEV")) {
                        showDevEditDialog(currentIssue, swingMemberController, swingIssueController, userId);
                    } else {
                        showTesterEditDialog(currentIssue, swingMemberController, swingIssueController, userId);
                    }
                }
        );

        if (memberResponseDto.getRole().toString().equals("ADMIN") || memberResponseDto.getRole().toString()
                .equals("PL") || memberResponseDto.getRole().toString().equals("DEV")) {
            gbc.gridy++;
            JButton reassignBtn = new JButton("이슈 담당자 변경");
            mainPanel.add(reassignBtn, gbc);

            reassignBtn.addActionListener(
                    e -> {
                        showReassignPage(currentIssue, currentProject, swingMemberController, swingIssueController,
                                userId);
                    }
            );
        }

        if (memberResponseDto.getRole().toString().equals("TESTER")) {
            gbc.gridy++;
            JButton deleteRequestBtn = new JButton("삭제 요청");
            mainPanel.add(deleteRequestBtn, gbc);

            deleteRequestBtn.addActionListener(
                    e -> {
                        IssueDeleteRequestDto issueDeleteRequestDto =
                                IssueDeleteRequestDto
                                        .builder()
                                        .issueId(currentIssue.getId())
                                        .build();
                        try {
                            swingIssueController.removeRequest(userId, issueDeleteRequestDto);
                            JOptionPane.showMessageDialog(this, "삭제 요청되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                            updateIssueInfo();
                            loadComments();
                            parentPage.refreshIssues();

                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(this, exception.getMessage(), "에러",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    }
            );
        }

        return mainPanel;
    }

    private void showIssueRecommendDialog(SwingIssueController swingIssueController, IssueResponseDto currentIssue,
                                          Long userId) {
        JDialog dialog = new JDialog(this, "이슈 추천 받기", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        dialog.add(new JLabel("이슈 유사도 목록"), gbc);

        issueRecommendDtoJList = new JList<>(new DefaultListModel<>());
        updateRecommendIssue();
        issueRecommendDtoJList.setCellRenderer(new IssueRecommendListRender());

        JScrollPane scrollPane = new JScrollPane(issueRecommendDtoJList);
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dialog.add(scrollPane, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private class IssueRecommendListRender extends JPanel implements ListCellRenderer<IssueRecommendResponseDto> {
        private JLabel score;
        private JLabel issueName;
        private JLabel issueState;
        private JLabel issuePriority;
        private JLabel issueAssignee;
        private JLabel issueReporter;
        private JLabel issueReportedDate;
        private JLabel issueFixer;
        private DateTimeFormatter formatter;

        public IssueRecommendListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");
            score = new JLabel();
            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(score, gbc);

            issueName = new JLabel();
            gbc.gridx = 1;
            add(issueName, gbc);

            issueState = new JLabel();
            gbc.gridx = 2;
            add(issueState, gbc);

            issuePriority = new JLabel();
            gbc.gridx = 3;
            add(issuePriority, gbc);

            issueAssignee = new JLabel();
            gbc.gridx = 4;
            add(issueAssignee, gbc);

            issueReporter = new JLabel();
            gbc.gridx = 5;
            add(issueReporter, gbc);

            issueReportedDate = new JLabel();
            gbc.gridx = 6;
            add(issueReportedDate, gbc);

            issueFixer = new JLabel();
            gbc.gridx = 7;
            add(issueFixer, gbc);


        }

        @Override
        public Component getListCellRendererComponent(JList<? extends IssueRecommendResponseDto> list,
                                                      IssueRecommendResponseDto value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            score.setText(value.getScore().toString() + "점");
            issueName.setText("[" + value.getIssueResponseDto().getTitle() + "]");
            issueState.setText("상태: " + value.getIssueResponseDto().getStatus().toString());
            issuePriority.setText("우선순위: " + value.getIssueResponseDto().getPriority().toString());
            issueAssignee.setText("담당자: " + ((value.getIssueResponseDto().getAssignee() == null) ? "없음"
                    : value.getIssueResponseDto().getAssignee().getName()));
            issueReporter.setText("보고자: " + value.getIssueResponseDto().getReporter().getName());
            issueReportedDate.setText("보고일자: " + value.getIssueResponseDto().getReportedDate().format(formatter));
            issueFixer.setText("해결한 사람: " + ((value.getIssueResponseDto().getFixer() == null) ? "없음"
                    : value.getIssueResponseDto().getFixer().getName()));

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

    private void updateRecommendIssue() {
        issueRecommendDtos = swingIssueController.recommendIssues(userId, currentIssue.getId());

        DefaultListModel<IssueRecommendResponseDto> listModel = (DefaultListModel<IssueRecommendResponseDto>) issueRecommendDtoJList.getModel();
        listModel.clear();

        for (IssueRecommendResponseDto issueRecommendResponseDto : issueRecommendDtos) {
            listModel.addElement(issueRecommendResponseDto);
        }
    }

    private void showReassignPage(IssueResponseDto currentIssue, ProjectResponseDto currentProject,
                                  SwingMemberController swingMemberController,
                                  SwingIssueController swingIssueController, Long userId) {
        JDialog dialog = new JDialog(this, "이슈 담당자 변경", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("지정 가능한 개발자 목록");
        dialog.add(title, gbc);

        devDtoJList = new JList<>(new DefaultListModel<>());
        updateDevList(swingMemberController, currentProject, userId);
        devDtoJList.setCellRenderer(new MemberListRender());

        devDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = devDtoJList.locationToIndex(e.getPoint());
                    MemberResponseDto selectedDev = devDtoJList.getModel().getElementAt(index);
                    showDevDetail(selectedDev, swingIssueController, currentIssue, userId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(devDtoJList);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDevDetail(MemberResponseDto selectedDev, SwingIssueController swingIssueController,
                               IssueResponseDto currentIssue, Long userId) {
        JDialog dialog = new JDialog(this, "이슈 담당자 변경", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel devName = new JLabel("이름: " + selectedDev.getName());
        dialog.add(devName, gbc);

        gbc.gridy = 1;
        JButton addBtn = new JButton("변경하기");
        dialog.add(addBtn, gbc);

        addBtn.addActionListener(
                e -> {
                    IssueAssignRequestDto issueAssignRequestDto =
                            IssueAssignRequestDto.builder()
                                    .issueId(currentIssue.getId())
                                    .assigneeId(selectedDev.getId())
                                    .build();
                    try {
                        swingIssueController.reassign(userId, issueAssignRequestDto);
                        JOptionPane.showMessageDialog(this, "변경 완료", "알림", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        updateIssueInfo();
                        loadComments();
                        parentPage.refreshIssues();
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(this, exception.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }

    class MemberListRender extends JPanel implements ListCellRenderer<MemberResponseDto> {
        private JLabel roleLabel;
        private JLabel nameLabel;

        public MemberListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            nameLabel = new JLabel();
            roleLabel = new JLabel();
            roleLabel.setFont(roleLabel.getFont().deriveFont(Font.PLAIN, 12f));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(nameLabel, gbc);

            gbc.gridx = 1;
            add(roleLabel, gbc);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends MemberResponseDto> list, MemberResponseDto value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(value.getName());
            roleLabel.setText(value.getRole().toString());
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


    private void updateDevList(SwingMemberController swingMemberController, ProjectResponseDto currentProject,
                               Long userId) {
        devDto = swingMemberController.findMembersByRole(userId, currentProject.getId(), Role.DEV);

        DefaultListModel<MemberResponseDto> listModel = (DefaultListModel<MemberResponseDto>) devDtoJList.getModel();
        listModel.clear();
        for (MemberResponseDto memberResponseDto : devDto) {
            listModel.addElement(memberResponseDto);
        }
    }

    private void showDevEditDialog(IssueResponseDto currentIssue, SwingMemberController swingMemberController,
                                   SwingIssueController swingIssueController, Long userId) {
        JDialog dialog = new JDialog(this, "이슈 수정", true);
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

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(new JLabel("카테고리: " + currentIssue.getCategory()), gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        dialog.add(new JLabel("상태: "), gbc);

        gbc.gridx = 1;
        Status[] statuses = {Status.ASSIGNED, Status.RESOLVED};

        JComboBox<Status> statusJComboBox = new JComboBox<>(statuses);
        statusJComboBox.setSelectedItem(currentIssue.getStatus());
        dialog.add(statusJComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(new JLabel("우선순위: " + currentIssue.getPriority().toString()), gbc);

        gbc.gridy = 4;
        dialog.add(new JLabel("설명"), gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextArea issueDescription = new JTextArea(currentIssue.getDescription());
        issueDescription.setLineWrap(true);
        issueDescription.setWrapStyleWord(true);
        issueDescription.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(issueDescription);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);

        gbc.gridy = 8;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        gbc.fill = GridBagConstraints.NONE;
        dialog.add(new JLabel("보고자: " + currentIssue.getReporter().getName()), gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");
        JLabel issueReportedDate = new JLabel("보고 일시: " + currentIssue.getReportedDate().format(formatter));
        gbc.gridy = 9;
        dialog.add(issueReportedDate, gbc);

        JLabel issueFixer = new JLabel(
                "해결한 사람 : " + (currentIssue.getFixer() == null ? "없음" : currentIssue.getFixer().getName()));
        gbc.gridy = 10;
        dialog.add(issueFixer, gbc);

        JLabel issueAssignee = new JLabel(
                "담당자: " + (currentIssue.getAssignee() == null ? "없음" : currentIssue.getAssignee().getName()));
        gbc.gridy = 11;
        dialog.add(issueAssignee, gbc);

        gbc.gridy = 12;
        gbc.gridwidth = 1;
        JButton editBtn = new JButton("수정");
        dialog.add(editBtn, gbc);

        editBtn.addActionListener(
                e -> {
                    IssueStatusUpdateRequestDto issueStatusUpdateRequestDto =
                            IssueStatusUpdateRequestDto
                                    .builder()
                                    .issueId(currentIssue.getId())
                                    .status((Status) statusJComboBox.getSelectedItem())
                                    .build();

                    try {
                        swingIssueController.updateDev(userId, issueStatusUpdateRequestDto);
                        JOptionPane.showMessageDialog(this, "수정 완료", "알림", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        updateIssueInfo();
                        loadComments();
                        parentPage.refreshIssues();

                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(this, exception.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }

    private void showTesterEditDialog(IssueResponseDto currentIssue, SwingMemberController swingMemberController,
                                      SwingIssueController swingIssueController, Long userId) {
        JDialog dialog = new JDialog(this, "이슈 수정", true);
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

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(new JLabel("카테고리: " + currentIssue.getCategory()), gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        dialog.add(new JLabel("상태: "), gbc);

        gbc.gridx = 1;
        Status[] statuses = {Status.CLOSED, Status.REOPENED};
        if (currentIssue.getStatus() != Status.CLOSED || currentIssue.getStatus() != Status.REOPENED) {
            statuses = new Status[]{currentIssue.getStatus(), Status.CLOSED, Status.REOPENED};
        }
        JComboBox<Status> statusJComboBox = new JComboBox<>(statuses);
        statusJComboBox.setSelectedItem(currentIssue.getStatus());
        dialog.add(statusJComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(new JLabel("우선순위: " + currentIssue.getPriority().toString()), gbc);

        gbc.gridy = 4;
        dialog.add(new JLabel("설명"), gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextArea issueDescription = new JTextArea(currentIssue.getDescription());
        issueDescription.setLineWrap(true);
        issueDescription.setWrapStyleWord(true);
        issueDescription.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(issueDescription);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);

        gbc.gridy = 8;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        gbc.fill = GridBagConstraints.NONE;
        dialog.add(new JLabel("보고자: " + currentIssue.getReporter().getName()), gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");
        JLabel issueReportedDate = new JLabel("보고 일시: " + currentIssue.getReportedDate().format(formatter));
        gbc.gridy = 9;
        dialog.add(issueReportedDate, gbc);

        JLabel issueFixer = new JLabel(
                "해결한 사람 : " + (currentIssue.getFixer() == null ? "없음" : currentIssue.getFixer().getName()));
        gbc.gridy = 10;
        dialog.add(issueFixer, gbc);

        JLabel issueAssignee = new JLabel(
                "담당자: " + (currentIssue.getAssignee() == null ? "없음" : currentIssue.getAssignee().getName()));
        gbc.gridy = 11;
        dialog.add(issueAssignee, gbc);

        gbc.gridy = 12;
        JButton editBtn = new JButton("수정");
        dialog.add(editBtn, gbc);

        editBtn.addActionListener(
                e -> {
                    IssueUpdateRequestDto issueStatusUpdateRequestDto =
                            IssueUpdateRequestDto
                                    .builder()
                                    .issueId(currentIssue.getId())
                                    .description(currentIssue.getDescription())
                                    .status((Status) statusJComboBox.getSelectedItem())
                                    .priority(currentIssue.getPriority())
                                    .category(currentIssue.getCategory())
                                    .build();
                    try {
                        swingIssueController.update(userId, issueStatusUpdateRequestDto);
                        JOptionPane.showMessageDialog(this, "수정 완료", "알림", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        updateIssueInfo();
                        loadComments();
                        parentPage.refreshIssues();


                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(this, exception.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);


    }

    private void showAdminPlEditDialog(IssueResponseDto currentIssue, SwingMemberController swingMemberController,
                                       SwingIssueController swingIssueController, Long userId) {
        JDialog dialog = new JDialog(this, "이슈 수정", true);
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

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(new JLabel("카테고리: " + currentIssue.getCategory()), gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        dialog.add(new JLabel("상태: "), gbc);

        gbc.gridx = 1;
        Status[] statuses = {Status.NEW, Status.ASSIGNED, Status.RESOLVED, Status.CLOSED, Status.REOPENED,
                Status.INACTIVE, Status.DELETE_REQUEST};

        JComboBox<Status> statusJComboBox = new JComboBox<>(statuses);
        statusJComboBox.setSelectedItem(currentIssue.getStatus());
        dialog.add(statusJComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        dialog.add(new JLabel("우선순위: "), gbc);

        gbc.gridx = 1;
        Priority[] priorities = {Priority.BLOCKER, Priority.CRITICAL, Priority.MAJOR, Priority.MINOR, Priority.TRIVIAL};
        JComboBox<Priority> priorityJComboBox = new JComboBox<>(priorities);
        priorityJComboBox.setSelectedItem(currentIssue.getPriority());
        dialog.add(priorityJComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialog.add(new JLabel("설명"), gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JTextArea issueDescription = new JTextArea(currentIssue.getDescription());
        issueDescription.setLineWrap(true);
        issueDescription.setWrapStyleWord(true);
        issueDescription.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(issueDescription);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);

        gbc.gridy = 8;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        gbc.fill = GridBagConstraints.NONE;
        dialog.add(new JLabel("보고자: " + currentIssue.getReporter().getName()), gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");
        JLabel issueReportedDate = new JLabel("보고 일시: " + currentIssue.getReportedDate().format(formatter));
        gbc.gridy = 9;
        dialog.add(issueReportedDate, gbc);

        JLabel issueFixer = new JLabel(
                "해결한 사람 : " + (currentIssue.getFixer() == null ? "없음" : currentIssue.getFixer().getName()));
        gbc.gridy = 10;
        dialog.add(issueFixer, gbc);

        JLabel issueAssignee = new JLabel(
                "담당자: " + (currentIssue.getAssignee() == null ? "없음" : currentIssue.getAssignee().getName()));
        gbc.gridy = 11;
        dialog.add(issueAssignee, gbc);

        gbc.gridy = 12;
        JButton editBtn = new JButton("수정");
        dialog.add(editBtn, gbc);

        editBtn.addActionListener(
                e -> {
                    IssueUpdateRequestDto issueStatusUpdateRequestDto =
                            IssueUpdateRequestDto
                                    .builder()
                                    .issueId(currentIssue.getId())
                                    .description(currentIssue.getDescription())
                                    .status((Status) statusJComboBox.getSelectedItem())
                                    .priority((Priority) priorityJComboBox.getSelectedItem())
                                    .category(currentIssue.getCategory())
                                    .build();
                    try {
                        swingIssueController.update(userId, issueStatusUpdateRequestDto);
                        JOptionPane.showMessageDialog(this, "수정 완료", "알림", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        updateIssueInfo();
                        loadComments();
                        parentPage.refreshIssues();


                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(this, exception.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

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
            MemberResponseDto member = swingMemberController.findMemberById(value.getWriter().getId());
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

    private void updateIssueInfo() {
        // 기존의 issueInfoPanel을 제거하고 새로 추가
        this.currentIssue = swingIssueController.getIssue(userId, currentIssue.getId());
        remove(issueInfoPanel);
        issueInfoPanel = initIssueInfo();
        add(issueInfoPanel, 0);
        revalidate();
        repaint();
    }


}
