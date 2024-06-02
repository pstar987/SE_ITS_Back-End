package com.se.its.view.pages;

import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.dto.request.MemberRoleUpdateRequestDto;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;

public class AccountRuleChangePage extends JFrame {
    private SwingMemberController swingMemberController;
    private final Long userId;
    private List<MemberResponseDto> memberResponseDtos;
    private JList<MemberResponseDto> memberResponseDtoJList;

    public AccountRuleChangePage(SwingMemberController swingMemberController, Long userId) {
        this.swingMemberController = swingMemberController;
        this.userId = userId;

        setTitle("계정 직책 변경");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        initData();
        initComponents();
    }

    private void initData() {
        memberResponseDtos = swingMemberController.findAllMembers(userId);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("계정 직책 변경");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;

        memberResponseDtoJList = new JList<>(new DefaultListModel<>());
        updateMemberResponseDtoJList();
        memberResponseDtoJList.setCellRenderer(new MemberListRender());

        JScrollPane scrollPane = new JScrollPane(memberResponseDtoJList);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);

        memberResponseDtoJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = memberResponseDtoJList.locationToIndex(e.getPoint());
                    MemberResponseDto selectedMember = memberResponseDtoJList.getModel().getElementAt(index);
                    showEditDialog(selectedMember);
                }
            }
        });
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

    Role getRole(String role) {
        switch (role) {
            case "PL":
                return Role.PL;
            case "DEV":
                return Role.DEV;
            default:
                return Role.TESTER;

        }
    }

    private void showEditDialog(MemberResponseDto memberResponseDto) {
        JDialog dialog = new JDialog(this, "직책 변경", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel idLabel = new JLabel("이름: " + memberResponseDto.getName());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(idLabel, gbc);

        JLabel roleLabel = new JLabel("Role:");
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dialog.add(roleLabel, gbc);

        String[] roles = {"PL", "DEV", "TESTER"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setSelectedItem(memberResponseDto.getRole().toString());
        gbc.gridx = 2;
        dialog.add(roleComboBox, gbc);

        JButton confirmBtn = new JButton("변경");
        confirmBtn.addActionListener(e -> {
            MemberRoleUpdateRequestDto memberRoleUpdateRequestDto =
                    MemberRoleUpdateRequestDto.builder()
                            .id(memberResponseDto.getId())
                            .role(getRole((String) roleComboBox.getSelectedItem()))
                            .build();


            MemberResponseDto responseDto = swingMemberController.updateMemberRole(userId, memberRoleUpdateRequestDto);
            for (int i = 0; i < memberResponseDtos.size(); i++) {
                if (memberResponseDtos.get(i).getId().equals(responseDto.getId())) {
                    memberResponseDtos.set(i, responseDto);
                    break;
                }
            }
            updateMemberResponseDtoJList();
            memberResponseDtoJList.repaint();
            dialog.dispose();
        });
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(confirmBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }

    private void updateMemberResponseDtoJList() {
        DefaultListModel<MemberResponseDto> listModel = (DefaultListModel<MemberResponseDto>) memberResponseDtoJList.getModel();
        listModel.clear();
        for (MemberResponseDto memberResponseDto : memberResponseDtos) {
            listModel.addElement(memberResponseDto);
        }
    }
}
