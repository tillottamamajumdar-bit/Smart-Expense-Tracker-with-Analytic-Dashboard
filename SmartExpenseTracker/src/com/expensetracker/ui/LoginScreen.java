package com.expensetracker.ui;

import com.expensetracker.database.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginScreen() {
        // Set up the main window properties
        setTitle("Smart Expense Tracker - Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the window on the screen
        setLayout(new GridLayout(4, 1, 10, 10)); // 4 rows, 1 column layout

        // Create UI Components
        JLabel titleLabel = new JLabel("Welcome Back!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel emailPanel = new JPanel();
        emailPanel.add(new JLabel("Email:      "));
        emailField = new JTextField(15);
        emailPanel.add(emailField);

        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passwordPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        buttonPanel.add(loginButton);

        // Add action to the Login Button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Add components to the window
        add(titleLabel);
        add(emailPanel);
        add(passwordPanel);
        add(buttonPanel);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        UserDAO userDAO = new UserDAO();
        boolean isValid = userDAO.loginUser(email, password);

        if (isValid) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome to your Dashboard.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // 1. Close the login window
            this.dispose(); 
            
            // 2. Open the new Dashboard window
            DashboardScreen dashboard = new DashboardScreen();
            dashboard.setVisible(true);
            
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}