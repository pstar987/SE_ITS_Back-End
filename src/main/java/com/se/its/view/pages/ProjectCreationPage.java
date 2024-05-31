package com.se.its.view.pages;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;

public class ProjectCreationPage extends JFrame {

    private JTextField projectName;


    public ProjectCreationPage() {
        setTitle("프로젝트 생성");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400,500);
        setLocationRelativeTo(null);


    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        gbc.gridx =0 ;
        gbc.gridy = 0;
        gbc.gridwidth= 2;
        gbc.anchor= GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("프로젝트 생성");
        titleLabel.setFont(new Font("Sanserif", Font.BOLD, 32));

        add(titleLabel, gbc);



    }
}
