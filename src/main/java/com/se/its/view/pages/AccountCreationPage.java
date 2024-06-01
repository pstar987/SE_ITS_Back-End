package com.se.its.view.pages;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

public class AccountCreationPage extends JFrame {
    private JTextField idTextField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JTextField nameTextField;
    private  JComboBox<String> roleComboBox;
    private JButton accountCreateBtn;


    public AccountCreationPage() {
        setTitle("계정 생성");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300,400);
        setLocationRelativeTo(null);

        initComponents();

    }

    private void initComponents()  {
        setLayout(new GridBagLayout());

        GridBagConstraints accountGbc = new GridBagConstraints();

        accountGbc.insets = new Insets(10, 10, 10, 10);

        accountGbc.gridx = 0;
        accountGbc.gridy = 0;
        accountGbc.gridwidth = 2;
        accountGbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("계정 생성");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        add(titleLabel, accountGbc);

        accountGbc.gridx = 0;
        accountGbc.gridy = 1;
        accountGbc.gridwidth = 1;
        accountGbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("아이디"), accountGbc);

        accountGbc.gridx = 1;
        accountGbc.gridy = 1;
        accountGbc.anchor = GridBagConstraints.WEST;
        idTextField = new JTextField(15);
        add(idTextField, accountGbc);

        accountGbc.gridx = 0;
        accountGbc.gridy = 2;
        accountGbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("비밀번호"), accountGbc);

        accountGbc.gridx = 1;
        accountGbc.gridy = 2;
        accountGbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(15);
        add(passwordField, accountGbc);

        accountGbc.gridx = 0;
        accountGbc.gridy = 3;
        accountGbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("비밀번호 확인"), accountGbc);

        accountGbc.gridx = 1;
        accountGbc.gridy = 3;
        accountGbc.anchor = GridBagConstraints.WEST;
        confirmPasswordField = new JPasswordField(15);
        add(confirmPasswordField, accountGbc);

        accountGbc.gridx = 0;
        accountGbc.gridy = 4;
        accountGbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("이름:"), accountGbc);

        accountGbc.gridx = 1;
        accountGbc.gridy = 4;
        accountGbc.anchor = GridBagConstraints.WEST;
        nameTextField = new JTextField(15);
        add(nameTextField, accountGbc);


        accountGbc.gridx = 0;
        accountGbc.gridy = 5;
        accountGbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("직책"), accountGbc);

        accountGbc.gridx = 1;
        accountGbc.gridy = 5;
        accountGbc.anchor = GridBagConstraints.WEST;

        //TODO 컨트롤러에서 직책 리스트 받기
        String[] roles = {"PL" ,"DEV", "TESTER" };
        roleComboBox = new JComboBox<>(roles);
        add(roleComboBox, accountGbc);


        accountGbc.gridx = 0;
        accountGbc.gridy = 6;
        accountGbc.gridwidth =  2;
        accountGbc.anchor = GridBagConstraints.CENTER;
        accountCreateBtn = new JButton("계정 생성하기");
        add(accountCreateBtn, accountGbc);

    }
}
