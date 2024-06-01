package com.se.its.view.pages;

import com.se.its.domain.member.domain.Role;
import com.se.its.domain.member.dto.request.MemberSignUpRequestDto;
import com.se.its.domain.member.dto.response.MemberResponseDto;
import com.se.its.domain.member.presentation.SwingMemberController;
import com.se.its.view.exception.ConfirmPasswordException;
import com.se.its.view.exception.EmptyIdException;
import com.se.its.view.exception.EmptyNameException;
import com.se.its.view.exception.EmptyPasswordException;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class AccountCreationPage extends JFrame {
    private JTextField idTextField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JTextField nameTextField;
    private JComboBox<String> roleComboBox;
    private JButton accountCreateBtn;

    private SwingMemberController swingMemberController;
    private final Long userId;
    public AccountCreationPage(SwingMemberController swingMemberController, Long userId) {
        this.swingMemberController = swingMemberController;
        this.userId = userId;

        setTitle("계정 생성");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 400);
        setLocationRelativeTo(null);

        initComponents();

    }

    private void initComponents() {
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

        String[] roles = {"PL", "DEV", "TESTER"};
        roleComboBox = new JComboBox<>(roles);
        add(roleComboBox, accountGbc);

        accountGbc.gridx = 0;
        accountGbc.gridy = 6;
        accountGbc.gridwidth = 2;
        accountGbc.anchor = GridBagConstraints.CENTER;
        accountCreateBtn = new JButton("계정 생성하기");
        add(accountCreateBtn, accountGbc);

        accountCreateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAccountCreation();
            }
        });
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

    private void handleAccountCreation() {
        String id = idTextField.getText();
        String password = new String(passwordField.getText());
        String confirmPassword = new String(confirmPasswordField.getText());
        String name = nameTextField.getText();
        Role role = getRole ((String) roleComboBox.getSelectedItem());

        if(checkValidation()) {
            MemberSignUpRequestDto requestDto = MemberSignUpRequestDto.builder()
                    .signId(id)
                    .password(password)
                    .name(name)
                    .role(role)
                    .build();
            try {
                MemberResponseDto responseDto = swingMemberController.signUp(userId, requestDto);

                JOptionPane.showMessageDialog(this, "계정 생성 성공", "계정 생성", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "계정 생성 실패: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }


    }
    private void checkIdEmpty() {
        if (idTextField.getText().isEmpty()) {
            throw new EmptyIdException();
        }
    }

    private void checkPassword() {
        if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            throw new EmptyPasswordException();
        }
    }
    private void checkName() {
        if(nameTextField.getText().isEmpty()) {
            throw new EmptyNameException();
        }
    }

    private void checkPasswordIsSame() {
        if(!passwordField.getText().equals(confirmPasswordField.getText())) {
            throw new ConfirmPasswordException();
        }
    }

    private boolean checkValidation() {
        try {
            checkIdEmpty();
            checkPassword();
            checkName();
            checkPasswordIsSame();
            return true;
        } catch (EmptyIdException | EmptyPasswordException| EmptyNameException | ConfirmPasswordException e) {
            showError(e.getMessage());
            return false;
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
