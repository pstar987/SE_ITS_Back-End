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

public class IssueSearchResultPage extends JFrame {

    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private SwingCommentController swingCommentController;
    private ProjectResponseDto currentProject;
    private final Long userId;
    private ProjectDetailPage parentPage;

    private List<IssueResponseDto> issueDtos;
    private JList<IssueResponseDto> issueDtoJList;

    public IssueSearchResultPage(ProjectDetailPage parentPage,SwingMemberController swingMemberController,
                                 SwingProjectController swingProjectController,
                                 SwingIssueController swingIssueController,
                                 SwingCommentController swingCommentController, ProjectResponseDto currentProject,
                                 List<IssueResponseDto> result, Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.swingCommentController = swingCommentController;
        this.issueDtos = result;
        this.userId = userId;
        this.parentPage = parentPage;

        setTitle("이슈 검색 결과");
        setSize(600, 600);
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

        JLabel titleLabel = new JLabel("이슈 검색 결과");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, gbc);

        if (issueDtos.isEmpty()) {
            JLabel resultIsEmpty = new JLabel("검색된 결과가 없습니다.");
            gbc.gridy = 1;
            add(resultIsEmpty, gbc);
        } else {
            issueDtoJList = new JList<>(new DefaultListModel<>());
            updateIssueList();
            issueDtoJList.setCellRenderer(new IssueListRender());

            issueDtoJList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = issueDtoJList.locationToIndex(e.getPoint());
                        IssueResponseDto selectedIssue = issueDtoJList.getModel().getElementAt(index);
                        new IssuePage(parentPage,swingMemberController, swingProjectController, swingIssueController,
                                swingCommentController, currentProject, selectedIssue, userId).setVisible(true);
                        dispose();
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(issueDtoJList);
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            add(scrollPane, gbc);
        }

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

    private void updateIssueList() {

        DefaultListModel<IssueResponseDto> listModel = (DefaultListModel<IssueResponseDto>) issueDtoJList.getModel();
        listModel.clear();
        for (IssueResponseDto issueResponseDto : issueDtos) {
            listModel.addElement(issueResponseDto);
        }
    }
}
