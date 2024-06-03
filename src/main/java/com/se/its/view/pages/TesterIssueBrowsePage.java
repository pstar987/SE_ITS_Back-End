package com.se.its.view.pages;

import com.se.its.domain.comment.presentation.SwingCommentController;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class TesterIssueBrowsePage extends JFrame {
    private ProjectDetailPage parentPage;
    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private SwingCommentController swingCommentController;
    private ProjectResponseDto currentProject;
    private final Long userId;
    private List<IssueResponseDto> issueResponseDtos;
    private JList<IssueResponseDto> issueResponseDtoJList;

    public TesterIssueBrowsePage(ProjectDetailPage parentPage, SwingMemberController swingMemberController,
                                 SwingProjectController swingProjectController,
                                 SwingIssueController swingIssueController,
                                 SwingCommentController swingCommentController, ProjectResponseDto currentProject,
                                 Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.swingCommentController = swingCommentController;
        this.currentProject = currentProject;
        this.userId = userId;

        setTitle("생성한 이슈 조회");
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

        JLabel titleLabel = new JLabel("생성한 이슈 조회");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(new JLabel("이슈 리스트"), gbc);

        issueResponseDtoJList = new JList<>(new DefaultListModel<>());
        initIssueList(userId, currentProject);
        issueResponseDtoJList.setCellRenderer(new IssueListRender());

        issueResponseDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = issueResponseDtoJList.locationToIndex(e.getPoint());
                    IssueResponseDto selectedIssue = issueResponseDtoJList.getModel().getElementAt(index);
                    new IssuePage(parentPage, swingMemberController, swingProjectController, swingIssueController,
                            swingCommentController, currentProject, selectedIssue, userId).setVisible(true);
                    dispose();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(issueResponseDtoJList);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

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

    void initIssueList(Long userId, ProjectResponseDto currentProject) {
        issueResponseDtos = swingIssueController.getTesterIssues(userId, currentProject.getId());
        DefaultListModel<IssueResponseDto> listModel = (DefaultListModel<IssueResponseDto>) issueResponseDtoJList.getModel();
        listModel.clear();
        for (IssueResponseDto issueResponseDto : issueResponseDtos) {
            listModel.addElement(issueResponseDto);
        }
    }
}
