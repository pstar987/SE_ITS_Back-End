package com.se.its.view.pages;

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
    public AccountRuleChangePage() {
        setTitle("계정 직책 변경");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        initComponents();
    }

//    private initData() {
//        accounts = new ArrayList<>();
//        Map<String, String> account1 = new HashMap<>();
//        account1.put("id", "user1");
//        account1.put("role", "PL");
//
//        Map<String, String> account2 = new HashMap<>();
//        account2.put("id", "user2");
//        account2.put("role", "DEV");
//
//        Map<String, String> account3 = new HashMap<>();
//        account3.put("id", "user3");
//        account3.put("role", "TESTER");
//
//        accounts.add(account1);
//        accounts.add(account2);
//        accounts.add(account3);
//    }

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
                if(e.getClickCount() ==2) {
                    int index = accountList.locationToIndex(e.getPoint());
                    Map<String, String> selectedAccount = accountList.getModel().getElementAt(index);

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

    private void showEditDialog(Map<String,String> account) {
        JDialog dialog = new JDialog(this, "직책 변경", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        JLabel idLabel = new JLabel("ID: "+ account.get("id"));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        dialog.add(idLabel, gbc);

        JLabel roleLabel = new JLabel("Role:");
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dialog.add(roleLabel, gbc);

        String []  roles = {"PL", "DEV","TESTER"};
     //   JComboBox<String>
    }

    class AccountListRender extends JPanel implements ListCellRenderer<Map<String, String>> {
        private JLabel idLabel;
        private JLabel roleLabel;

        public AccountListRender() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            idLabel = new JLabel();
            roleLabel = new JLabel();
            roleLabel.setFont(roleLabel.getFont().deriveFont(Font.PLAIN, 12f));

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            add(idLabel, gbc);

            gbc.gridx = 1;
            add(roleLabel, gbc);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Map<String, String>> list,
                                                      Map<String, String> account, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            idLabel.setText(account.get("id"));
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
