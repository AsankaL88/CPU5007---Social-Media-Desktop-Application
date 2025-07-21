package main.java.com.socialmedia.gui;

import com.socialmedia.exception.DatabaseException;
import com.socialmedia.exception.InvalidCredentialsException;
import main.java.com.socialmedia.exception.UserAlreadyExistsException;
import main.java.com.socialmedia.model.User;
import main.java.com.socialmedia.service.ChannelService;
import main.java.com.socialmedia.service.MessageService;
import main.java.com.socialmedia.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login and registration frame for the social media application.
 * Provides user authentication and account creation functionality.
 */
public class LoginFrame extends JFrame {
    
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JLabel statusLabel;
    
    public LoginFrame(UserService userService, ChannelService channelService, MessageService messageService) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageService = messageService;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        setTitle("Social Media Application - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeComponents() {
        emailField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");
        statusLabel = new JLabel(" ");
        
        emailField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        loginButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        signUpButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        signUpButton.setBackground(new Color(40, 167, 69));
        signUpButton.setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(248, 249, 250));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Social Media Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);
        
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(new LoginActionListener());
        signUpButton.addActionListener(new SignUpActionListener());
        
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(40, 167, 69));
    }
    
    private void clearForm() {
        emailField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
    
    private void openChannelsFrame(User user) {
        SwingUtilities.invokeLater(() -> {
            try {
                ChannelsFrame channelsFrame = new ChannelsFrame(user, channelService, messageService);
                channelsFrame.setVisible(true);
                dispose();
            } catch (Exception e) {
                System.err.println("Failed to open channels frame");
                e.printStackTrace();
                showStatus("Failed to open channels page", true);
            }
        });
    }
    
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (email.isEmpty() || password.isEmpty()) {
                showStatus("Please enter both email and password", true);
                return;
            }
            
            loginButton.setEnabled(false);
            signUpButton.setEnabled(false);
            showStatus("Authenticating...", false);
            
            SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
                @Override
                protected User doInBackground() throws Exception {
                    return userService.authenticateUser(email, password);
                }
                
                @Override
                protected void done() {
                    try {
                        User user = get();
                        System.out.println("User logged in successfully: " + user.getEmail());
                        openChannelsFrame(user);
                    } catch (Exception ex) {
                        System.err.println("Login failed");
                        ex.printStackTrace();
                        if (ex.getCause() instanceof InvalidCredentialsException) {
                            showStatus("Invalid email or password", true);
                        } else {
                            showStatus("Login failed: " + ex.getMessage(), true);
                        }
                    } finally {
                        loginButton.setEnabled(true);
                        signUpButton.setEnabled(true);
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    private class SignUpActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (email.isEmpty() || password.isEmpty()) {
                showStatus("Please enter both email and password", true);
                return;
            }
            
            if (password.length() < 6) {
                showStatus("Password must be at least 6 characters long", true);
                return;
            }
            
            loginButton.setEnabled(false);
            signUpButton.setEnabled(false);
            showStatus("Creating account...", false);
            
            SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
                @Override
                protected User doInBackground() throws Exception {
                    return userService.registerUser(email, password);
                }
                
                @Override
                protected void done() {
                    try {
                        User user = get();
                        System.out.println("User registered successfully: " + user.getEmail());
                        showStatus("Account created successfully! Please login.", false);
                        clearForm();
                    } catch (Exception ex) {
                        System.err.println("Registration failed");
                        ex.printStackTrace();
                        if (ex.getCause() instanceof UserAlreadyExistsException) {
                            showStatus("The email address entered has already been registered.", true);
                        } else {
                            showStatus("Registration failed: " + ex.getMessage(), true);
                        }
                    } finally {
                        loginButton.setEnabled(true);
                        signUpButton.setEnabled(true);
                    }
                }
            };
            
            worker.execute();
        }
    }
}
