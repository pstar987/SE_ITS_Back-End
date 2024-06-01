package com.se.its.view.pages;

import com.se.its.domain.member.application.MemberService;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class AccountRuleChangePage extends JFrame {
    private List<Map<String, String>> accounts;
    private JList<Map<String, String>> accountList;

    private SwingMemberController swingMemberController;
    private final Long userId;

    private List<MemberResponseDto> memberResponseDtos;

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
        memberResponseDtos = new ArrayList<>();
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

        accountList = new JList<>(accounts.toArray(new HashMap[0]));
        accountList.setCellRenderer(new AccountListRender());

        accountList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = accountList.locationToIndex(e.getPoint());
                    Map<String, String> selectedAccount = accountList.getModel().getElementAt(index);
                    showEditDialog(selectedAccount);

                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(accountList);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);
    }

    private void showEditDialog(Map<String, String> account) {
        JDialog dialog = new JDialog(this, "직책 변경", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel idLabel = new JLabel("ID: " + account.get("id"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(idLabel, gbc);

        JLabel nameLabel = new JLabel("이름: " + account.get("name"));
        gbc.gridy = 1;
        dialog.add(nameLabel, gbc);

        JLabel roleLabel = new JLabel("Role:");
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        dialog.add(roleLabel, gbc);

        String[] roles = {"PL", "DEV", "TESTER"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setSelectedItem(account.get("role"));
        gbc.gridx = 2;
        dialog.add(roleComboBox, gbc);

        JButton confirmBtn = new JButton("변경");
        confirmBtn.addActionListener(e -> {
            account.put("role", (String) roleComboBox.getSelectedItem());
            accountList.repaint();
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

    class AccountListRender extends JPanel implements ListCellRenderer<Map<String, String>> {
        private JLabel idLabel;
        private JLabel roleLabel;
        private JLabel nameLabel;

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

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//                    new AccountRuleChangePage().setVisible(true);
//                }
//        );
//    }
}
