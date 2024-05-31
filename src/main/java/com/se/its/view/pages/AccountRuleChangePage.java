package com.se.its.view.pages;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

public class AccountRuleChangePage extends JFrame {
    public AccountRuleChangePage() {
        setTitle("계정 직책 변경");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300,400);
        setLocationRelativeTo(null);

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

    }

}
