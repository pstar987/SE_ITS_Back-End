package com.se.its.view.pages;

import com.se.its.domain.issue.dto.request.IssueAssignRequestDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class SelectedAssigneePage extends JFrame {

    private IssueResponseDto currentIssue;
    private ProjectResponseDto currentProject;
    private SwingMemberController swingMemberController;
    private SwingIssueController swingIssueController;
    private final Long userId;

    private SetIssueAssigneePage parentPage;

    private List<MemberResponseDto> devDtos;
    private JList<MemberResponseDto> devDtosJList;

    public SelectedAssigneePage(SetIssueAssigneePage parentPage, IssueResponseDto selectedIssue,
                                ProjectResponseDto currentProject,
                                SwingMemberController swingMemberController,
                                SwingIssueController swingIssueController, Long userId) {
        this.currentIssue = selectedIssue;
        this.currentProject = currentProject;
        this.swingMemberController = swingMemberController;
        this.swingIssueController = swingIssueController;
        this.userId = userId;
        this.parentPage = parentPage;

        setTitle("담당자 지정하기");
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
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel issueTitle = new JLabel(currentIssue.getTitle());
        issueTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(issueTitle, gbc);

        JLabel issueCategory = new JLabel(
                currentIssue.getCategory() == null ? "카테고리: 없음" : "카테고리: " + currentIssue.getCategory());
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(issueCategory, gbc);

        JLabel issueStatus = new JLabel("상태: " + currentIssue.getStatus().toString());
        gbc.gridy = 2;
        add(issueStatus, gbc);

        JLabel issuePriority = new JLabel("우선 순위: " + currentIssue.getPriority().toString());
        gbc.gridy = 3;
        add(issuePriority, gbc);

        gbc.gridy = 4;
        add(new JLabel("설명"), gbc);

        JTextArea issueDescription = new JTextArea(currentIssue.getDescription());
        issueDescription.setLineWrap(true);
        issueDescription.setWrapStyleWord(true);
        issueDescription.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(issueDescription);

        gbc.gridy = 5;
        gbc.gridwidth =2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(scrollPane, gbc);

        JLabel issueReporter = new JLabel("보고자: " + currentIssue.getReporter().getName());
        gbc.gridy = 6;
        add(issueReporter, gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");
        JLabel issueReportedDate = new JLabel("보고 일시: " + currentIssue.getReportedDate().format(formatter));
        gbc.gridy = 7;
        add(issueReportedDate, gbc);

        JButton assignedIssueBtn = new JButton("담당자 지정");
        gbc.gridy = 8;
        add(assignedIssueBtn, gbc);

        assignedIssueBtn.addActionListener(
                e -> {
                    showDevInfoDialog(currentIssue, swingMemberController, swingIssueController, userId);
                }
        );
    }


    private void showDevInfoDialog(IssueResponseDto currentIssue, SwingMemberController swingMemberController,
                                   SwingIssueController swingIssueController, Long userId) {
        JDialog dialog = new JDialog(this, "이슈 담당자 지정", true);

        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("지정 가능한 개발자");
        dialog.add(title, gbc);

        devDtosJList = new JList<>(new DefaultListModel<>());
        initDevList(swingMemberController, currentProject, userId);
        devDtosJList.setCellRenderer(new MemberListRender());

        devDtosJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = devDtosJList.locationToIndex(e.getPoint());
                    MemberResponseDto selectedDev = devDtosJList.getModel().getElementAt(index);
                    showDevDetailDialog(selectedDev, currentIssue, swingIssueController);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(devDtosJList);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);

        dialog.setSize(200, 300);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }

    private void showDevDetailDialog(MemberResponseDto selectedDev, IssueResponseDto currentIssue,
                                     SwingIssueController swingIssueController) {
        JDialog dialog = new JDialog(this, "담당자 지정", true);
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
        JButton addBtn = new JButton("추가하기");
        dialog.add(addBtn, gbc);

        addBtn.addActionListener(
                e -> {
                    assignDevToIssue(selectedDev, currentIssue, userId, swingIssueController);
                    dialog.dispose();
                    this.dispose();
                    parentPage.refrestIssueList();
                }
        );

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }

    private void assignDevToIssue(MemberResponseDto selectedDev, IssueResponseDto currentIssue, Long userId,
                                  SwingIssueController swingIssueController) {
        IssueAssignRequestDto issueAssignRequestDto =
                IssueAssignRequestDto
                        .builder()
                        .issueId(currentIssue.getId())
                        .assigneeId(selectedDev.getId())
                        .build();
        swingIssueController.assign(userId, issueAssignRequestDto);

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


    private void initDevList(SwingMemberController swingMemberController, ProjectResponseDto currentProject,
                             Long userId) {
        devDtos = swingMemberController.findMembersByRole(userId, currentProject.getId(), Role.DEV);
        DefaultListModel<MemberResponseDto> listModel = (DefaultListModel<MemberResponseDto>) devDtosJList.getModel();
        listModel.clear();
        for (MemberResponseDto memberResponseDto : devDtos) {
            listModel.addElement(memberResponseDto);
        }

    }
}
