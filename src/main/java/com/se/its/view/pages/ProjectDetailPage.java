package com.se.its.view.pages;

import com.se.its.domain.comment.presentation.SwingCommentController;
import com.se.its.domain.issue.dto.request.IssueDeleteRequestDto;
import com.se.its.domain.issue.dto.response.IssueResponseDto;
import com.se.its.domain.issue.presentation.SwingIssueController;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.response.ProjectResponseDto;
import com.se.its.domain.project.presentation.SwingProjectController;
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


public class ProjectDetailPage extends JFrame {

    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private SwingIssueController swingIssueController;
    private SwingCommentController swingCommentController;
    private ProjectResponseDto currentProject;
    private final Long userId;
    private List<IssueResponseDto> issueDtos;
    private JList<IssueResponseDto> issueDtoJList;

    private List<IssueResponseDto> removeIssueDtos;
    private JList<IssueResponseDto> removeIssueDtoJList;

    public ProjectDetailPage(ProjectResponseDto selectedProject, SwingMemberController swingMemberController,
                             SwingProjectController swingProjectController, SwingIssueController swingIssueController,
                             SwingCommentController swingCommentController,
                             Long userId) {
        this.currentProject = selectedProject;
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.swingIssueController = swingIssueController;
        this.swingCommentController = swingCommentController;
        this.userId = userId;

        initComponents();

        setTitle("프로젝트 상세 정보");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel(currentProject.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel subTitle = new JLabel("이슈 리스트");
        add(subTitle, gbc);

        issueDtoJList = new JList<>(new DefaultListModel<>());
        initIssueData(currentProject);
        issueDtoJList.setCellRenderer(new IssueListRender());

        issueDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = issueDtoJList.locationToIndex(e.getPoint());
                    IssueResponseDto selectedIssue = issueDtoJList.getModel().getElementAt(index);
                    new IssuePage(ProjectDetailPage.this, swingMemberController, swingProjectController,
                            swingIssueController, swingCommentController, currentProject, selectedIssue,
                            userId).setVisible(true);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(issueDtoJList);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton issueSearchBtn = new JButton("이슈 검색하기");
        //TODO 이슈 검색 페이지
        add(issueSearchBtn, gbc);

        issueSearchBtn.addActionListener(
                e -> {
                    new IssueSearchPage(ProjectDetailPage.this, currentProject, swingMemberController,
                            swingProjectController, swingIssueController, swingCommentController, userId).setVisible(
                            true);
                }
        );

        checkUserIsADMINAndPLAndInitComponent();
        checkUserIsDEVandInitComponent();
        checkUserIsTesterAndInitComponent();
    }


    private void checkUserIsTesterAndInitComponent() {
        MemberResponseDto member = swingMemberController.findMemberById(userId);
        if (member.getRole().toString().equals("TESTER")) {
            initTESTERComponent();
        }
    }

    private void initTESTERComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton browseMyIssueBtn = new JButton("내가 생성한 이슈 조회하기");

        add(browseMyIssueBtn, gbc);

        browseMyIssueBtn.addActionListener(
                e -> {
                    new TesterIssueBrowsePage(ProjectDetailPage.this, swingMemberController, swingProjectController,
                            swingIssueController, swingCommentController, currentProject, userId).setVisible(true);
                }
        );

        gbc.gridy = 5;
        JButton createIssueBtn = new JButton("이슈 생성하기");
        add(createIssueBtn, gbc);

        createIssueBtn.addActionListener(
                e -> new IssueCreationPage(currentProject, swingIssueController, userId, this).setVisible(true)
        );
    }

    private void checkUserIsDEVandInitComponent() {
        MemberResponseDto member = swingMemberController.findMemberById(userId);
        if (member.getRole().toString().equals("DEV")) {
            initDEVComponent();
        }
    }

    public void refreshIssues() {
        currentProject = swingProjectController.getProject(userId, currentProject.getId());
        initIssueData(currentProject);
    }

    private void initDEVComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton browseAssignedIssueBtn = new JButton("할당된 이슈 조회하기");
        add(browseAssignedIssueBtn, gbc);
        browseAssignedIssueBtn.addActionListener(
                e -> {
                    new DevIssueBrowsePage(ProjectDetailPage.this, swingMemberController, swingProjectController,
                            swingIssueController, swingCommentController, currentProject, userId).setVisible(true);
                }
        );
    }

    private void checkUserIsADMINAndPLAndInitComponent() {
        MemberResponseDto member = swingMemberController.findMemberById(userId);
        if (member.getRole().toString().equals("PL") || member.getRole().toString().equals("ADMIN")) {
            initADMINPLComponent();
        }
    }

    private void initADMINPLComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton assignDevToIssueBtn = new JButton("이슈 담당자 지정");
        add(assignDevToIssueBtn, gbc);

        assignDevToIssueBtn.addActionListener(
                e -> {
                    new SetIssueAssigneePage(ProjectDetailPage.this, swingMemberController, swingIssueController,
                            currentProject, userId).setVisible(true);
                }
        );

        gbc.gridy = 5;
        JButton changeIssueStatusBtn = new JButton("이슈 상태 변경");
        add(changeIssueStatusBtn, gbc);

        changeIssueStatusBtn.addActionListener(
                e -> {
                    new SetIssueStatusPage(ProjectDetailPage.this, swingIssueController, currentProject,
                            userId).setVisible(true);
                }
        );

        gbc.gridy = 6;
        JButton setIssuePriorityBtn = new JButton("이슈 우선순위 설정");
        add(setIssuePriorityBtn, gbc);

        setIssuePriorityBtn.addActionListener(
                e -> {
                    new SetIssuePriorityPage(ProjectDetailPage.this, swingIssueController, currentProject,
                            userId).setVisible(true);
                }
        );
        if (swingMemberController.findMemberById(userId).getRole().toString().equals("ADMIN")) {
            gbc.gridy = 7;
            JButton removeIssueBtn = new JButton("이슈 삭제하기");
            add(removeIssueBtn, gbc);

            removeIssueBtn.addActionListener(
                    e -> {
                        showRemoveRequestIssues(swingIssueController, userId);
                    }
            );
        }


    }

    private void showRemoveRequestIssues(SwingIssueController swingIssueController, Long userId) {
        JDialog dialog = new JDialog(this, "이슈 삭제하기", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("삭제 요청 이슈");
        dialog.add(titleLabel, gbc);

        removeIssueDtoJList = new JList<>(new DefaultListModel<>());
        updateRemoveIssueList();
        removeIssueDtoJList.setCellRenderer(new IssueListRender());

        removeIssueDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = removeIssueDtoJList.locationToIndex(e.getPoint());
                    IssueResponseDto selectedIssue = removeIssueDtoJList.getModel().getElementAt(index);
                    showRemoveIssuesDetail(selectedIssue);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(removeIssueDtoJList);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dialog.add(scrollPane, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showRemoveIssuesDetail(IssueResponseDto currentIssue) {
        JDialog dialog = new JDialog(this, "이슈 삭제하기", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel issueTitle = new JLabel(currentIssue.getTitle());
        issueTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        dialog.add(issueTitle, gbc);

        JLabel issueCategory = new JLabel(
                currentIssue.getCategory() == null ? "카테고리: 없음" : "카테고리: " + currentIssue.getCategory());
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(issueCategory, gbc);

        JLabel issueStatus = new JLabel("상태: " + currentIssue.getStatus().toString());
        gbc.gridy = 2;
        dialog.add(issueStatus, gbc);

        JLabel issuePriority = new JLabel("우선 순위: " + currentIssue.getPriority().toString());
        gbc.gridy = 3;
        dialog.add(issuePriority, gbc);

        gbc.gridy = 4;
        dialog.add(new JLabel("설명"), gbc);

        JTextArea issueDescription = new JTextArea(currentIssue.getDescription());
        issueDescription.setLineWrap(true);
        issueDescription.setWrapStyleWord(true);
        issueDescription.setEditable(false);
        issueDescription.setPreferredSize(new Dimension(200, 200));

        JScrollPane scrollPane = new JScrollPane(issueDescription);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(scrollPane, gbc);

        JLabel issueReporter = new JLabel("보고자: " + currentIssue.getReporter().getName());
        gbc.gridy = 6;
        dialog.add(issueReporter, gbc);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm");
        JLabel issueReportedDate = new JLabel("보고 일시: " + currentIssue.getReportedDate().format(formatter));
        gbc.gridy = 7;
        dialog.add(issueReportedDate, gbc);

        JLabel issueFixer = new JLabel(
                "해결한 사람 : " + (currentIssue.getFixer() == null ? "없음" : currentIssue.getFixer().getName()));
        gbc.gridy = 8;
        dialog.add(issueFixer, gbc);

        JLabel issueAssignee = new JLabel(
                "담당자: " + (currentIssue.getAssignee() == null ? "없음" : currentIssue.getAssignee().getName()));
        gbc.gridy = 9;
        dialog.add(issueAssignee, gbc);

        gbc.gridy = 10;
        JButton deleteBtn = new JButton("삭제");
        dialog.add(deleteBtn, gbc);
        deleteBtn.addActionListener(
                e -> {
                    IssueDeleteRequestDto issueDeleteRequestDto =
                            IssueDeleteRequestDto
                                    .builder()
                                    .issueId(currentIssue.getId())
                                    .build();
                    try {
                        swingIssueController.remove(userId, issueDeleteRequestDto);
                        JOptionPane.showMessageDialog(this, "삭제되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        updateRemoveIssueList();
                        refreshIssues();
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(this, exception.getMessage(), "에러", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateRemoveIssueList() {
        removeIssueDtos = swingIssueController.getRemoveRequestIssues(userId);
        System.out.println(removeIssueDtos.toArray().length);
        DefaultListModel<IssueResponseDto> listModel = (DefaultListModel<IssueResponseDto>) removeIssueDtoJList.getModel();
        listModel.clear();

        for (IssueResponseDto issueResponseDto : removeIssueDtos) {
            listModel.addElement(issueResponseDto);
        }
    }

    public void refreshIssueList() {
        initIssueData(currentProject);
        issueDtoJList.repaint();
        issueDtoJList.revalidate();
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
            issueStatus.setText("상태: " + value.getStatus().toString());
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

    private void initIssueData(ProjectResponseDto projectDto) {
        currentProject = swingProjectController.getProject(userId, projectDto.getId());
        issueDtos = swingIssueController.getIssues(userId, currentProject.getId());
        DefaultListModel<IssueResponseDto> listModel = (DefaultListModel<IssueResponseDto>) issueDtoJList.getModel();
        listModel.clear();
        for (IssueResponseDto issueResponseDto : issueDtos) {
            listModel.addElement(issueResponseDto);
        }
    }


}
