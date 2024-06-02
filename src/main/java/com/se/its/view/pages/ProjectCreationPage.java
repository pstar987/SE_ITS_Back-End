package com.se.its.view.pages;

import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.request.ProjectCreateRequestDto;
import com.se.its.domain.project.presentation.SwingProjectController;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class ProjectCreationPage extends JFrame {

    private JTextField projectNameTextField;
    private List<Map<String, String>> accounts;
    private JList<Map<String, String>> accountList;

    private List<MemberResponseDto> memberResponseDtos;
    private JList<MemberResponseDto> memberResponseDtoJList;

    private List<MemberResponseDto> projectMemberDtos;
    private JList<MemberResponseDto> projectMemberDtoJList;
    private JList<MemberResponseDto> projectMemberDtoJListForDelete;


    private JButton memberAddBtn;
    private JButton memberDeleteBtn;
    private JButton projectCreateBtn;
    private SwingMemberController swingMemberController;

    private SwingProjectController swingProjectController;
    private Long userId;

    public ProjectCreationPage(SwingMemberController swingMemberController,
                               SwingProjectController swingProjectController, Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.userId = userId;

        setTitle("프로젝트 생성");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        initData();
        initComponents();
    }

    private void initData() {
        memberResponseDtos = swingMemberController.findAllMembers(userId);
        projectMemberDtos = new ArrayList<>();
    }

    private void updateMemberResponseDtoJList() {
        DefaultListModel<MemberResponseDto> listModel = (DefaultListModel<MemberResponseDto>) memberResponseDtoJList.getModel();
        listModel.clear();
        for (MemberResponseDto memberResponseDto : memberResponseDtos) {
            listModel.addElement(memberResponseDto);
        }
    }

    private void updateProjectMember() {
        DefaultListModel<MemberResponseDto> model = (DefaultListModel<MemberResponseDto>) projectMemberDtoJList.getModel();
        model.clear();
        for (MemberResponseDto memberResponseDto : projectMemberDtos) {
            model.addElement(memberResponseDto);
        }
    }

    private void updateProjectMember(JList<MemberResponseDto> list) {
        DefaultListModel<MemberResponseDto> model = (DefaultListModel<MemberResponseDto>) list.getModel();
        model.clear();
        for (MemberResponseDto memberResponseDto : projectMemberDtos) {
            model.addElement(memberResponseDto);
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("프로젝트 생성");
        titleLabel.setFont(new Font("Sanserif", Font.BOLD, 32));
        add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("프로젝트 이름:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        projectNameTextField = new JTextField(15);
        add(projectNameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JLabel("프로젝트 멤버"), gbc);

        gbc.gridy = 3;

        projectMemberDtoJList = new JList<>(new DefaultListModel<>());
        projectMemberDtoJList.setCellRenderer(new MemberListRender());
        add(new JScrollPane(projectMemberDtoJList), gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        memberAddBtn = new JButton("멤버 추가하기");
        memberAddBtn.addActionListener(e -> showAddMemberDialog());
        add(memberAddBtn, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        memberDeleteBtn = new JButton("멤버 삭제하기");
        memberDeleteBtn.addActionListener(e -> showDeleteDialog());
        add(memberDeleteBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        projectCreateBtn = new JButton("프로젝트 생성");
        projectCreateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createProject();
            }
        });
        add(projectCreateBtn, gbc);

    }

    private void createProject() {
        String projectName = projectNameTextField.getText();
        if (projectName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "프로젝트 이름을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Long> projectMemberId = new ArrayList<>();
        for (MemberResponseDto memberResponseDto : projectMemberDtos) {
            projectMemberId.add(memberResponseDto.getId());
        }

        ProjectCreateRequestDto projectCreateRequestDto =
                ProjectCreateRequestDto
                        .builder()
                        .name(projectName)
                        .memberIds(projectMemberId)
                        .build();
        try {
            swingProjectController.createProject(userId, projectCreateRequestDto);
            JOptionPane.showMessageDialog(this, "프로젝트 생성 완료");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void showDeleteDialog() {
        JDialog dialog = new JDialog(this, "프로젝트 멤버 삭제", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("프로젝트 멤버 삭제하기");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        dialog.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;

        projectMemberDtoJListForDelete = new JList<>(new DefaultListModel<>());
        updateProjectMember(projectMemberDtoJListForDelete);
        projectMemberDtoJListForDelete.setCellRenderer(new MemberListRender());

        projectMemberDtoJListForDelete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    MemberResponseDto selectedMember = projectMemberDtoJListForDelete.getSelectedValue();
                    if (selectedMember != null) {
                        showDeleteDetailDialog(selectedMember);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "선택된 멤버가 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(projectMemberDtoJListForDelete);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);


    }

    private void showDeleteDetailDialog(MemberResponseDto selectedMember) {
        JDialog dialog = new JDialog(this, "멤버 삭제", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel idLabel = new JLabel("이름: " + selectedMember.getName());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(idLabel, gbc);

        JLabel roleLabel = new JLabel("직책: " + selectedMember.getRole().toString());
        gbc.gridy = 1;
        dialog.add(roleLabel, gbc);
        JButton deleteBtn = new JButton("삭제");
        deleteBtn.addActionListener(
                e -> {
                    deleteMemberToProject(selectedMember);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(ProjectCreationPage.this, "멤버가 삭제되었습니다.");
                }
        );

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        dialog.add(deleteBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteMemberToProject(MemberResponseDto selectedMember) {
        projectMemberDtos.removeIf(member -> member.getId() == selectedMember.getId());
        updateProjectMember();
        updateProjectMember(projectMemberDtoJListForDelete);
        projectMemberDtoJList.updateUI();
    }

    private void showAddMemberDialog() {
        JDialog dialog = new JDialog(this, "프로젝트 멤버 추가", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("프로젝트 멤버 추가하기");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        dialog.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;

        memberResponseDtoJList = new JList<>(new DefaultListModel<>());
        updateMemberResponseDtoJList();
        memberResponseDtoJList.setCellRenderer(new MemberListRender());

        memberResponseDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = memberResponseDtoJList.locationToIndex(e.getPoint());
                    MemberResponseDto selectedMember = memberResponseDtoJList.getModel().getElementAt(index);
                    showAddDetailDialog(selectedMember);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(memberResponseDtoJList);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddDetailDialog(MemberResponseDto selectedMember) {
        JDialog dialog = new JDialog(this, "멤버 추가", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel idLabel = new JLabel("이름: " + selectedMember.getName());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(idLabel, gbc);

        JLabel roleLabel = new JLabel("직책: " + selectedMember.getRole().toString());
        gbc.gridy = 1;
        dialog.add(roleLabel, gbc);
        JButton addBtn = new JButton("추가");
        addBtn.addActionListener(
                e -> {
                    addMemberToProject(selectedMember);
                    dialog.dispose();
                }
        );

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(addBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addMemberToProject(MemberResponseDto selectedMember) {
        for (MemberResponseDto memberResponseDto : projectMemberDtos) {
            if (memberResponseDto.getId() == selectedMember.getId()) {
                JOptionPane.showMessageDialog(this, "이 계정은 이미 프로젝트에 있습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        projectMemberDtos.add(selectedMember);
        updateProjectMember();
    }

    private class MemberListRender extends JPanel implements ListCellRenderer<MemberResponseDto> {
        private JLabel idLabel;
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


    private class AccountListRender extends JPanel implements ListCellRenderer<Map<String, String>> {
        private JLabel idLabel;
        private JLabel nameLabel;
        private JLabel roleLabel;

        public AccountListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            idLabel = new JLabel();
            nameLabel = new JLabel();
            roleLabel = new JLabel();
            roleLabel.setFont(roleLabel.getFont().deriveFont(Font.PLAIN, 12f));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(idLabel, gbc);

            gbc.gridx = 1;
            add(nameLabel, gbc);
            gbc.gridx = 2;
            add(roleLabel, gbc);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Map<String, String>> list,
                                                      Map<String, String> account, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            idLabel.setText(account.get("id"));
            nameLabel.setText(account.get("name"));
            roleLabel.setText(account.get("role"));

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
