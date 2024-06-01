package com.se.its.view.pages;

import com.se.its.view.pages.AccountRuleChangePage.AccountListRender;
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

public class AccountDeletePage extends JFrame {

    private List<Map<String, String>> accounts;
    private JList<Map<String, String>> accountList;

    public AccountDeletePage() {
        setTitle("계정 삭제");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);
        initData();
        initComponents();

    }
    private void updateList() {
        accountList.setListData(accounts.toArray(new HashMap[0]));
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
    }


    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("계정 삭제하기");
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
                    showEditDialog(selectedAccount, index);

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

    private void showEditDialog(Map<String, String> account, int index) {
        JDialog dialog = new JDialog(this, "계정 삭제", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel idLabel = new JLabel("ID: " + account.get("id"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        dialog.add(idLabel, gbc);

        JLabel nameLabel = new JLabel("Name : " + account.get("name"));
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        dialog.add(nameLabel, gbc);

        JLabel roleLabel = new JLabel("Role: " + account.get("role"));
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        dialog.add(roleLabel, gbc);

        JButton deleteBtn = new JButton("삭제");
        deleteBtn.addActionListener(e -> {
            accounts.remove(index);
            updateList();
            dialog.dispose();
        });
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(deleteBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

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
