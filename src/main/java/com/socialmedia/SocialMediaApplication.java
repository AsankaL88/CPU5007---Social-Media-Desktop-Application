package main.java.com.socialmedia;

import main.java.com.socialmedia.database.DatabaseManager;
import main.java.com.socialmedia.gui.LoginFrame;
import main.java.com.socialmedia.service.ChannelService;
import main.java.com.socialmedia.service.MessageService;
import main.java.com.socialmedia.service.UserService;

import javax.swing.*;

/**
 * Main application class for the Social Media Desktop Application. Initializes
 * the database, services, and starts the GUI.
 */
public class SocialMediaApplication {

    public static void main(String[] args) {
        try {
            // Set system Look and Feel (default)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Initialize database
            DatabaseManager.getInstance().initializeDatabase();
            System.out.println("Database initialized successfully");

            // Initialize services
            UserService userService = new UserService();
            ChannelService channelService = new ChannelService();
            MessageService messageService = new MessageService();

            // Initialize default channel
            channelService.createDefaultChannel();
            System.out.println("Default channel created");

            // Start GUI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    LoginFrame loginFrame = new LoginFrame(userService, channelService, messageService);
                    loginFrame.setVisible(true);
                    System.out.println("Application started successfully");
                } catch (Exception e) {
                    System.err.println("Failed to start application GUI");
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Failed to start application: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            });

        } catch (Exception e) {
            System.err.println("Failed to initialize application");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to initialize application: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
