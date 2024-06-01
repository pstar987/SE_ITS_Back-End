package com.se.its.view.pages;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.springframework.security.core.parameters.P;


class Member {
    public String name;
    public String id;
    public String role;

    public Member() {
    }

    public Member(String name, String id, String role) {
        this.name = name;
        this.id = id;
        this.role = role;
    }
}

class Project {
    public String name;
    public List<Member> members = new ArrayList<>();

    Project() {

    }

    public Project(String name) {
        this.name = name;
    }

    public void addMember(Member member) {
        members.add(member);
    }

}

public class ProjectMangePage extends JFrame {

    List<Project> projects;
    JList<Project> projectList;

    List<Member> members;

    public ProjectMangePage() {
        setTitle("프로젝트 관리");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        initData();
        initComponents();
    }

    private void initData() {
        Member member1 = new Member("이영재", "id1", "PL");
        Member member2 = new Member("이해강", "id2", "DEV");
        Member member3 = new Member("강민규", "id3", "TESTER");
        Member member4 = new Member("정상제", "id4", "PL");
        Member member5 = new Member("홍길동", "id5", "DEV");

        members.add(member1);
        members.add(member2);
        members.add(member3);
        members.add(member4);
        members.add(member5);

        Project project1 = new Project("소공1");
        project1.addMember(member1);
        project1.addMember(member2);
        project1.addMember(member3);

        Project project2 = new Project("소공2");
        project2.addMember(member4);
        project2.addMember(member5);

        Project project3 = new Project("소공3");
        project3.addMember(member1);
        project3.addMember(member2);
        project3.addMember(member5);

        projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);

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

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;

        projectList = new JList<>(projects.toArray(new Project[]{new Project()}));
        projectList.setCellRenderer(new ProjectListRender());

        projectList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = projectList.locationToIndex(e.getPoint());
                    Project selectedProject = projectList.getModel().getElementAt(index);
                    showEditDialog(selectedProject);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(projectList);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);
    }

    private void showEditDialog(Project selectedProject) {
        JDialog dialog = new JDialog(this, "프로젝트 관리");
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel projectNameLabel = new JLabel("프로젝트 이름: " + selectedProject.name);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(projectNameLabel, gbc);

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(new JLabel("프로젝트 멤버"), gbc);

        List<Member> members = selectedProject.members;
        JList<Member> memberJList = new JList<>(members.toArray(new Member[]{new Member()}));
        memberJList.setCellRenderer(new MemberListRender());

        JScrollPane scrollPane = new JScrollPane(memberJList);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(scrollPane, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JButton addMemberBtn = new JButton("멤버 추가");

        addMemberBtn.addActionListener(e -> showAddMemberDialog());

        dialog.add(addMemberBtn, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JButton deleteMemberBtn = new JButton("멤버 삭제");
        dialog.add(deleteMemberBtn, gbc);

        dialog.setSize(400, 450);
        dialog.setResizable(true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddMemberDialog() {
        JDialog dialog = new JDialog(this, "멤버 추가", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JList<Member> memberJList = new JList<>(members.toArray(new Member[]{new Member()}));
        memberJList.setCellRenderer(new MemberListRender());

        dialog.add(new JScrollPane(memberJList), gbc);
        dialog.setSize(300,400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    class MemberListRender extends JPanel implements ListCellRenderer<Member> {
        private JLabel idLabel;
        private JLabel roleLabel;
        private JLabel nameLabel;

        public MemberListRender() {
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
        public Component getListCellRendererComponent(JList<? extends Member> list, Member value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            System.out.println(value.name);

            idLabel.setText(value.id);
            nameLabel.setText(value.name);
            roleLabel.setText(value.role);

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

    class ProjectListRender extends JPanel implements ListCellRenderer<Project> {
        private JLabel projectNameLabel;

        public ProjectListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            projectNameLabel = new JLabel();
            projectNameLabel.setFont(projectNameLabel.getFont().deriveFont(Font.PLAIN, 12f));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(projectNameLabel, gbc);
        }


        @Override
        public Component getListCellRendererComponent(JList<? extends Project> list, Project value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            projectNameLabel.setText(value.name);
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
