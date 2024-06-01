package com.se.its.view.pages;

import com.se.its.view.exception.EmptyIdException;
import com.se.its.view.exception.EmptyPasswordException;
import com.se.its.view.httpCilent.ApiClient;
import com.se.its.view.util.ErrorMessage;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import org.springframework.security.core.parameters.P;

public class LoginPage extends JFrame {

    //TODO 로그인 시 계정의 직책에 따라 페이지가 달라져야됨

    private final String url = "http://3.34.107.220:8080/api/v1";

    private String ID = "admin";
    private String PASSWORD = "1234";

    private JTextField idTextField;
    private JPasswordField pwTextField;
    private JButton signInBtn;
    private JPanel mainPanel;

    public LoginPage() {

        initComponents();
        ActionListener signInAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignIn();
            }
        };
        setSignInAction(signInAction);
    }

    private void handleSignIn() {
        if (checkValidation()) {
            String id = idTextField.getText();
            String password = new String(pwTextField.getText());
            try {
                if (authenticateUser(id, password)) {
                    JOptionPane.showMessageDialog(signInBtn, "로그인 성공");
                    new AdminPage();
                    dispose();
                } else {
                    showError(ErrorMessage.FAILED_TO_SIGNIN.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("로그인 오류 발생");
            }

        }
    }

    private boolean authenticateUser(String id, String password) throws Exception {
        String url = "http://3.34.107.220:8080/api/v1/member/signIn";
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "*/*");
        headers.put("Content-Type", "application/json");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("signId", id);
        requestBody.put("password", password);

        HttpResponse<String> response = ApiClient.post(url, headers, requestBody);

        // 여기서는 단순히 "성공"이라는 문자열을 반환한다고 가정합니다.
        // 실제 응답 형식에 맞게 수정해야 합니다.
        return response.statusCode() == 200;
    }

    private void initComponents() {
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 간의 간격 설정

        // 로그인 레이블
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(new JLabel("로그인"), gbc);

        // ID 레이블
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("ID:"), gbc);

        // ID 텍스트 필드
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        idTextField = new JTextField(15);
        mainPanel.add(idTextField, gbc);

        // PASSWORD 레이블
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("PASSWORD:"), gbc);

        // PASSWORD 텍스트 필드
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        pwTextField = new JPasswordField(15);
        mainPanel.add(pwTextField, gbc);

        // 로그인 버튼
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        signInBtn = new JButton("로그인");
        mainPanel.add(signInBtn, gbc);

        add(mainPanel);
        setTitle("CITS");
        setSize(300, 240);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void checkIdEmpty() {
        if (idTextField.getText().isEmpty()) {
            throw new EmptyIdException();
        }
    }

    private void checkPassword() {
        if (pwTextField.getText().isEmpty()) {
            throw new EmptyPasswordException();
        }
    }

    private boolean checkValidation() {
        try {
            checkIdEmpty();
            checkPassword();
            return true;
        } catch (EmptyIdException | EmptyPasswordException e) {
            showError(e.getMessage());
            return false;
        }
    }


    public String getUserId() {
        return idTextField.getText();
    }

    public String getPassword() {
        return pwTextField.getText();
    }

    public void setSignInAction(ActionListener actionListener) {
        signInBtn.addActionListener(actionListener);
        idTextField.addActionListener(actionListener);
        pwTextField.addActionListener(actionListener);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        LoginPage loginPage = new LoginPage();
    }

}

