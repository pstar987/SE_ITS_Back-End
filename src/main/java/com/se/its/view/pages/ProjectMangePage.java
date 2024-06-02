package com.se.its.view.pages;

import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.domain.project.dto.request.ProjectMemberAddRequestDto;
import com.se.its.domain.project.dto.request.ProjectMemberRemoveRequestDto;
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
import java.util.List;
import javax.swing.*;

public class ProjectMangePage extends JFrame {

    private SwingMemberController swingMemberController;
    private SwingProjectController swingProjectController;
    private final Long userId;

    private List<ProjectResponseDto> projectResponseDtos;
    private JList<ProjectResponseDto> projectResponseDtoJList;

    private List<MemberResponseDto> projectMemberDtos;
    private JList<MemberResponseDto> projectMemberDtoJList;

    private List<MemberResponseDto> memberResponseDtos;
    private JList<MemberResponseDto> memberResponseDtoJList;

    private JList<MemberResponseDto> projectMemberDtoJListForDelete;

    public ProjectMangePage(SwingMemberController swingMemberController, SwingProjectController swingProjectController,
                            Long userId) {
        this.swingMemberController = swingMemberController;
        this.swingProjectController = swingProjectController;
        this.userId = userId;

        setTitle("프로젝트 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        initData();
        initComponents();
    }

    private void initData() {
        projectResponseDtos = swingProjectController.getAllProject(userId);
    }

    private void updateProjectList() {
        DefaultListModel<ProjectResponseDto> listModel = (DefaultListModel<ProjectResponseDto>) projectResponseDtoJList.getModel();
        listModel.clear();
        initData();
        for (ProjectResponseDto projectResponseDto : projectResponseDtos) {
            listModel.addElement(projectResponseDto);
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

        JLabel titleLabel = new JLabel("프로젝트 관리");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(new JLabel("프로젝트 리스트"), gbc);

        projectResponseDtoJList = new JList<>(new DefaultListModel<>());
        updateProjectList();
        projectResponseDtoJList.setCellRenderer(new ProjectListRender());

        projectResponseDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = projectResponseDtoJList.locationToIndex(e.getPoint());
                    ProjectResponseDto selectedProject = projectResponseDtoJList.getModel().getElementAt(index);
                    showProjectDetail(selectedProject);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(projectResponseDtoJList);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

    }

    private void initMemberData(ProjectResponseDto projectResponseDto) {
        projectMemberDtos = projectResponseDto.getMembers();
    }

    private void updateMemberList(ProjectResponseDto selectedProject) {
        ProjectResponseDto projectResponseDto = swingProjectController.getProject(userId, selectedProject.getId());
        initMemberData(projectResponseDto);
        DefaultListModel<MemberResponseDto> listModel = (DefaultListModel<MemberResponseDto>) projectMemberDtoJList.getModel();
        listModel.clear();
        initData();
        for (MemberResponseDto memberResponseDto : projectMemberDtos) {
            listModel.addElement(memberResponseDto);
        }

    }

    private void showProjectDetail(ProjectResponseDto selectedProject) {
        JDialog dialog = new JDialog(this, "프로젝트 관리", true);
        dialog.setSize(300, 400);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel(selectedProject.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        dialog.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(new JLabel("프로젝트 참여 인원"), gbc);

        projectMemberDtoJList = new JList<>(new DefaultListModel<>());
        updateMemberList(selectedProject);
        projectMemberDtoJList.setCellRenderer(new MemberListRender());

        JScrollPane scrollPane = new JScrollPane(projectMemberDtoJList);
        scrollPane.setPreferredSize(new Dimension(150, 200));
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        dialog.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.WEST;

        JButton addBtn = new JButton("추가");
        addBtn.addActionListener(e -> {
            showAddMemberDialog(selectedProject);
        });
        dialog.add(addBtn, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JButton deleteBtn = new JButton("삭제");
        deleteBtn.addActionListener(e -> showDeleteDialog(selectedProject));
        dialog.add(deleteBtn, gbc);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDeleteDialog(ProjectResponseDto selectedProject) {
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
                        showDeleteDetailDialog(selectedMember, selectedProject);
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

    private void showDeleteDetailDialog(MemberResponseDto selectedMember, ProjectResponseDto selectedProject) {
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
                    deleteMemberToProject(selectedMember, selectedProject);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "멤버가 삭제되었습니다.");
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

    private void deleteMemberToProject(MemberResponseDto selectedMember, ProjectResponseDto selectedProject) {
        ProjectMemberRemoveRequestDto projectMemberRemoveRequestDto =
                ProjectMemberRemoveRequestDto
                        .builder()
                        .removeMemberId(selectedMember.getId())
                        .build();
        swingProjectController.removeMember(userId, selectedProject.getId(), projectMemberRemoveRequestDto);
        updateMemberList(selectedProject);
        updateProjectMember(projectMemberDtoJListForDelete);
    }

    private void updateProjectMember(JList<MemberResponseDto> list) {
        DefaultListModel<MemberResponseDto> model = (DefaultListModel<MemberResponseDto>) list.getModel();
        model.clear();
        for (MemberResponseDto memberResponseDto : projectMemberDtos) {
            model.addElement(memberResponseDto);
        }
    }

    private void showAddMemberDialog(ProjectResponseDto selectedProject) {
        JDialog dialog = new JDialog(this, "프로젝트 멤버 추가", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("프로젝트 멤버 추가하기");
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
                    showAddDetailDialog(selectedMember, selectedProject);
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

    private void initAllMember() {
        memberResponseDtos = swingMemberController.findAllMembers(userId);
    }

    private void updateMemberResponseDtoJList() {
        initAllMember();
        DefaultListModel<MemberResponseDto> listModel = (DefaultListModel<MemberResponseDto>) memberResponseDtoJList.getModel();
        listModel.clear();
        for (MemberResponseDto memberResponseDto : memberResponseDtos) {
            listModel.addElement(memberResponseDto);
        }
    }

    private void showAddDetailDialog(MemberResponseDto selectedMember, ProjectResponseDto selectedProject) {
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
                    addMemberToProject(selectedMember, selectedProject);
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

    private void addMemberToProject(MemberResponseDto selectedMember, ProjectResponseDto selectedProject) {
        for (MemberResponseDto memberResponseDto : projectMemberDtos) {
            if (memberResponseDto.getId() == selectedMember.getId()) {
                JOptionPane.showMessageDialog(this, "이 계정은 이미 프로젝트에 있습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        ProjectMemberAddRequestDto projectMemberAddRequestDto =
                ProjectMemberAddRequestDto
                        .builder()
                        .addMemberId(selectedMember.getId())
                        .build();
        try {
            swingProjectController.addMember(userId, selectedProject.getId(), projectMemberAddRequestDto);
            updateMemberList(selectedProject);
            //TODO 지우기 디버그용
            for (MemberResponseDto memberResponseDto : projectMemberDtos) {
                System.out.println(memberResponseDto.getName() + " " + memberResponseDto.getRole());
            }


        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    class MemberListRender extends JPanel implements ListCellRenderer<MemberResponseDto> {

        private JLabel nameLabel;
        private JLabel roleLabel;

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

    class ProjectListRender extends JPanel implements ListCellRenderer<ProjectResponseDto> {
        private JLabel nameLabel;

        public ProjectListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            nameLabel = new JLabel();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(nameLabel, gbc);

        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ProjectResponseDto> list,
                                                      ProjectResponseDto value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            nameLabel.setText(value.getName());
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
