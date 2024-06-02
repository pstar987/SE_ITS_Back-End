package com.se.its.view.pages;

import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

public class SetIssueAssigneePage extends JFrame {

    private SwingIssueController swingIssueController;
    private SwingMemberController swingMemberController;

    private ProjectResponseDto currentProject;
    private final Long userId;

    private ProjectDetailPage parentPage;

    private List<IssueResponseDto> newStatusIssueDtos;
    private JList<IssueResponseDto> newStatusIssueDtoJList;

    public SetIssueAssigneePage(ProjectDetailPage parentPage, SwingMemberController swingMemberController,
                                SwingIssueController swingIssueController,
                                ProjectResponseDto currentProject, Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingIssueController = swingIssueController;
        this.currentProject = currentProject;
        this.userId = userId;
        this.parentPage = parentPage;

        setTitle("이슈 담당자 지정");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponent();
    }

    private void initComponent() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("이슈 담당자 지정");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel subTitle = new JLabel("담당자를 지정할 수 있는 이슈 목록");
        add(subTitle, gbc);

        newStatusIssueDtoJList = new JList<>(new DefaultListModel<>());
        initNewStatusIssue(currentProject, swingIssueController, userId);
        newStatusIssueDtoJList.setCellRenderer(new IssueListRender());

        newStatusIssueDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = newStatusIssueDtoJList.locationToIndex(e.getPoint());
                    IssueResponseDto selectedIssue = newStatusIssueDtoJList.getModel().getElementAt(index);
                    new SelectedAssigneePage(SetIssueAssigneePage.this, selectedIssue, currentProject,
                            swingMemberController, swingIssueController,
                            userId).setVisible(true);

                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(newStatusIssueDtoJList);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);
    }

    public void refrestIssueList() {
        initNewStatusIssue(currentProject, swingIssueController, userId);
        newStatusIssueDtoJList.repaint();
        newStatusIssueDtoJList.revalidate();
        parentPage.refreshIssueList();
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

    private void initNewStatusIssue(ProjectResponseDto currentProject, SwingIssueController swingIssueController,
                                    Long userId) {
        List<IssueResponseDto> totalIssue = swingIssueController.getIssues(userId, currentProject.getId());
        newStatusIssueDtos = new ArrayList<>();
        for (IssueResponseDto issueResponseDto : totalIssue) {
            if (issueResponseDto.getStatus().toString().equals("NEW")) {
                newStatusIssueDtos.add(issueResponseDto);
            }
        }
        DefaultListModel<IssueResponseDto> listModel = (DefaultListModel<IssueResponseDto>) newStatusIssueDtoJList.getModel();
        listModel.clear();
        for (IssueResponseDto issueResponseDto : newStatusIssueDtos) {
            listModel.addElement(issueResponseDto);
        }
    }
}

