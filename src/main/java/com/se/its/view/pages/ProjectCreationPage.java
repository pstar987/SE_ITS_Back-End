package com.se.its.view.pages;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class ProjectCreationPage extends JFrame {

    private JTextField projectNameTextField;
    private List<Map<String, String>> accounts;
    private JList<Map<String, String>> accountList;

    private List<Map<String, String>> projectMembers;

    private JList<Map<String, String>> projectMemberList;

    private JButton memberAddBtn;
    private JButton memberDeleteBtn;
    private JButton projectCreateBtn;

    public ProjectCreationPage() {
        setTitle("프로젝트 생성");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        initData();
        initComponents();
    }

    private void initData() {
        accounts = new ArrayList<>();
        Map<String, String> account1 = new HashMap<>();
        account1.put("id", "user1");
        account1.put("name", "이영재");
        account1.put("role", "PL");

        Map<String, String> account2 = new HashMap<>();
        account2.put("id", "user2");
        account2.put("name", "강민규");
        account2.put("role", "DEV");

        Map<String, String> account3 = new HashMap<>();
        account3.put("id", "user3");
        account3.put("name", "이해강");
        account3.put("role", "TESTER");

        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);

        projectMembers = new ArrayList<>();
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
        projectMemberList = new JList<>(new DefaultListModel<>());
        projectMemberList.setCellRenderer(new AccountListRender());
        add(new JScrollPane(projectMemberList), gbc);

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
        add(memberDeleteBtn,gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        projectCreateBtn = new JButton("프로젝트 생성");
        projectCreateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(projectCreateBtn,"프로젝트 생성 완료" );
                dispose();
            }
        });
        add(projectCreateBtn, gbc);

    }

    private void showAddMemberDialog() {
        JDialog dialog = new JDialog(this, "멤버 추가", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JList<Map<String, String>> accountList = new JList<>(accounts.toArray(new HashMap[0]));
        accountList.setCellRenderer(new AccountListRender());
        accountList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = accountList.locationToIndex(e.getPoint());
                    Map<String, String> selectedAccount = accountList.getModel().getElementAt(index);
                    addMemberToProject(selectedAccount);
                    dialog.dispose();
                }
            }

        });

        dialog.add(new JScrollPane(accountList), gbc);
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDeleteDialog() {
        JDialog dialog = new JDialog(this, "멤버 삭제", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JList<Map<String, String>> memberList = new JList<>(projectMembers.toArray(new HashMap[0]));
        memberList.setCellRenderer(new AccountListRender());
        memberList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2) {
                    int index = memberList.locationToIndex(e.getPoint());
                    Map<String, String> selectedMember = memberList.getModel().getElementAt(index);
                    removeMemberFromProject(selectedMember);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(ProjectCreationPage.this, "멤버가 삭제되었습니다.");
                }
            }
        });

        dialog.add(new JScrollPane(memberList), gbc);
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void removeMemberFromProject(Map<String, String> account) {
        projectMembers.removeIf(member -> member.get("id").equals(account.get("id")));
        updateProjectMemberList();
    }

    private void addMemberToProject(Map<String, String> account) {
        for (Map<String, String> member : projectMembers) {
            if (member.get("id").equals(account.get("id"))) {
                JOptionPane.showMessageDialog(this, "이 계정은 이미 프로젝트에 있습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        projectMembers.add(account);
        updateProjectMemberList();

    }

    private void updateProjectMemberList() {
        DefaultListModel<Map<String, String>> model = (DefaultListModel<Map<String, String>>) projectMemberList.getModel();
        model.clear();
        for (Map<String, String> member : projectMembers) {
            model.addElement(member);
        }
    }


    class AccountListRender extends JPanel implements ListCellRenderer<Map<String, String>> {
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
