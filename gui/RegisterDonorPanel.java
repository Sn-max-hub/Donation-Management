package gui;

import app.GlobalConstants;
import models.Donor;
import services.DonorService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLIntegrityConstraintViolationException;

public class RegisterDonorPanel extends JPanel {
    public RegisterDonorPanel(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(GlobalConstants.LIGHT_BLUE_COLOR);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(GlobalConstants.LABEL_FONT);
        usernameLabel.setForeground(Color.BLACK);
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(GlobalConstants.INPUT_FONT);
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(GlobalConstants.TEXT_FIELD_BORDER);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(GlobalConstants.LABEL_FONT);
        emailLabel.setForeground(Color.BLACK);
        JTextField emailField = new JTextField(20);
        emailField.setFont(GlobalConstants.INPUT_FONT);
        emailField.setBackground(Color.WHITE);
        emailField.setBorder(GlobalConstants.TEXT_FIELD_BORDER);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(GlobalConstants.LABEL_FONT);
        nameLabel.setForeground(Color.BLACK);
        JTextField nameField = new JTextField(20);
        nameField.setFont(GlobalConstants.INPUT_FONT);
        nameField.setBackground(Color.WHITE);
        nameField.setBorder(GlobalConstants.TEXT_FIELD_BORDER);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(GlobalConstants.LABEL_FONT);
        addressLabel.setForeground(Color.BLACK);
        JTextField addressField = new JTextField(20);
        addressField.setFont(GlobalConstants.INPUT_FONT);
        addressField.setBackground(Color.WHITE);
        addressField.setBorder(GlobalConstants.TEXT_FIELD_BORDER);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(GlobalConstants.LABEL_FONT);
        passwordLabel.setForeground(Color.BLACK);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(GlobalConstants.INPUT_FONT);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(GlobalConstants.TEXT_FIELD_BORDER);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(GlobalConstants.LABEL_FONT);
        confirmPasswordLabel.setForeground(Color.BLACK);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(GlobalConstants.INPUT_FONT);
        confirmPasswordField.setBackground(Color.WHITE);
        confirmPasswordField.setBorder(GlobalConstants.TEXT_FIELD_BORDER);

        // Add components to formPanel
        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.insets = new Insets(10, 10, 10, 10);
        gbcButton.anchor = GridBagConstraints.CENTER;

        JButton registerButton = new JButton("Register Donor");
        registerButton.setFont(GlobalConstants.LABEL_FONT);
        registerButton.setBackground(GlobalConstants.BUTTON_BG_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setFocusPainted(false);

        registerButton.addActionListener(e -> {
            StringBuilder errors = new StringBuilder();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.length() < 6) errors.append("- Username must be at least 6 characters\n");
            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) errors.append("- Invalid email format\n");
            if (name.isEmpty()) errors.append("- Name cannot be empty\n");
            if (address.isEmpty()) errors.append("- Address cannot be empty\n");
            if (password.length() < 8) errors.append("- Password must be at least 8 characters\n");
            if (!password.equals(confirmPassword)) errors.append("- Passwords do not match\n");

            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this, errors.toString(), "Validation Errors", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    DonorService donorService = new DonorService();
                    Donor donor = donorService.registerDonorUser(username, password, email, name, address);
                    if (donor != null) {
                        JOptionPane.showMessageDialog(this, "Donor registered successfully!");
                        cardLayout.show(mainPanel, "LOGIN");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error occurred. Try again!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLIntegrityConstraintViolationException ex) {
                    if (ex.getMessage().contains("username")) {
                        JOptionPane.showMessageDialog(this, "Username already taken!", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                    } else if (ex.getMessage().contains("email")) {
                        JOptionPane.showMessageDialog(this, "Email already in use!", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel loginLabel = new JLabel("<html>Already have an account? <u>Login</u></html>");
        loginLabel.setFont(GlobalConstants.LABEL_FONT);
        loginLabel.setForeground(Color.BLACK);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, "LOGIN");
            }
        });

        gbcButton.gridwidth = GridBagConstraints.REMAINDER;
        buttonPanel.add(registerButton, gbcButton);
        buttonPanel.add(loginLabel, gbcButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, GlobalConstants.SECONDARY_COLOR, 0, getHeight(), GlobalConstants.LIGHT_BLUE_COLOR);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}

