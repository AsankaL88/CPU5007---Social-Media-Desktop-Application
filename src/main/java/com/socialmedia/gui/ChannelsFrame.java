package main.java.com.socialmedia.gui;

import com.socialmedia.exception.DatabaseException;
import main.java.com.socialmedia.model.Channel;
import main.java.com.socialmedia.model.User;
import main.java.com.socialmedia.service.ChannelService;
import main.java.com.socialmedia.service.MessageService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Frame for displaying available channels and managing subscriptions.
 * Allows users to view, subscribe to, and access channels.
 */
public class ChannelsFrame extends JFrame {
    private final User currentUser;
    private final ChannelService channelService;
    private final MessageService messageService;
    
    private JPanel channelsPanel;
    private JTextField searchField;
    private JLabel statusLabel;
    private final Map<Integer, JButton> subscriptionButtons = new ConcurrentHashMap<>();
    
    public ChannelsFrame(User currentUser, ChannelService channelService, MessageService messageService) {
        this.currentUser = currentUser;
        this.channelService = channelService;
        this.messageService = messageService;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadChannels();
        
        setTitle("Social Media Application - Channels");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        channelsPanel = new JPanel();
        channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.Y_AXIS));
        channelsPanel.setBackground(Color.WHITE);
        
        searchField = new JTextField();
        searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        searchField.setEnabled(false); // Low priority feature
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Available Channels", SwingConstants.LEFT);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getEmail(), SwingConstants.RIGHT);
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        userLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel searchLabel = new JLabel("Search Channels:");
        searchLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        searchLabel.setForeground(Color.GRAY);
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(channelsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        statusPanel.setBackground(new Color(248, 249, 250));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        searchField.addActionListener(e -> {
            showStatus("Search functionality coming soon!", false);
        });
    }
    
    private void loadChannels() {
        SwingWorker<List<Channel>, Void> worker = new SwingWorker<List<Channel>, Void>() {
            @Override
            protected List<Channel> doInBackground() throws Exception {
                return channelService.getAllChannels();
            }
            
            @Override
            protected void done() {
                try {
                    List<Channel> channels = get();
                    displayChannels(channels);
                } catch (Exception e) {
                    System.err.println("Failed to load channels");
                    e.printStackTrace();
                    showStatus("Failed to load channels: " + e.getMessage(), true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void displayChannels(List<Channel> channels) {
        channelsPanel.removeAll();
        subscriptionButtons.clear();
        
        for (Channel channel : channels) {
            JPanel channelPanel = createChannelPanel(channel);
            channelsPanel.add(channelPanel);
            channelsPanel.add(Box.createVerticalStrut(10));
        }
        
        channelsPanel.revalidate();
        channelsPanel.repaint();
        
        showStatus("Loaded " + channels.size() + " channel(s)", false);
    }
    
    private JPanel createChannelPanel(Channel channel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setBackground(Color.WHITE);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(channel.getName());
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        nameLabel.setForeground(new Color(0, 123, 255));
        nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JLabel descLabel = new JLabel(channel.getDescription());
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(descLabel, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton subscribeButton = new JButton();
        subscribeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        subscribeButton.setPreferredSize(new Dimension(100, 30));
        
        updateSubscriptionButton(channel, subscribeButton);
        subscriptionButtons.put(channel.getId(), subscribeButton);
        
        actionPanel.add(subscribeButton);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.EAST);
        
        nameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openChannelHome(channel);
            }
        });
        
        return panel;
    }
    
    private void updateSubscriptionButton(Channel channel, JButton button) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return channelService.isUserSubscribed(currentUser.getId(), channel.getId());
            }
            
            @Override
            protected void done() {
                try {
                    boolean isSubscribed = get();
                    
                    if (isSubscribed) {
                        button.setText("Unsubscribe");
                        button.setBackground(new Color(220, 53, 69));
                        button.setForeground(Color.WHITE);
                        button.addActionListener(e -> unsubscribeFromChannel(channel));
                    } else {
                        button.setText("Subscribe");
                        button.setBackground(new Color(40, 167, 69));
                        button.setForeground(Color.WHITE);
                        button.addActionListener(e -> subscribeToChannel(channel));
                    }
                    
                } catch (Exception e) {
                    System.err.println("Failed to check subscription status");
                    e.printStackTrace();
                    button.setText("Error");
                    button.setEnabled(false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void subscribeToChannel(Channel channel) {
        JButton button = subscriptionButtons.get(channel.getId());
        if (button != null) {
            button.setEnabled(false);
            button.setText("...");
        }
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                channelService.subscribeUser(currentUser.getId(), channel.getId());
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    System.out.println("User " + currentUser.getEmail() + " subscribed to channel " + channel.getName());
                    showStatus("Successfully subscribed to " + channel.getName(), false);
                    
                    if (button != null) {
                        button.removeActionListener(button.getActionListeners()[0]);
                        button.setText("Unsubscribe");
                        button.setBackground(new Color(220, 53, 69));
                        button.addActionListener(e -> unsubscribeFromChannel(channel));
                        button.setEnabled(true);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Failed to subscribe to channel");
                    e.printStackTrace();
                    showStatus("Failed to subscribe to " + channel.getName(), true);
                    
                    if (button != null) {
                        button.setText("Subscribe");
                        button.setEnabled(true);
                    }
                }
            }
        };
        
        worker.execute();
    }
    
    private void unsubscribeFromChannel(Channel channel) {
        JButton button = subscriptionButtons.get(channel.getId());
        if (button != null) {
            button.setEnabled(false);
            button.setText("...");
        }
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                channelService.unsubscribeUser(currentUser.getId(), channel.getId());
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    System.out.println("User " + currentUser.getEmail() + " unsubscribed from channel " + channel.getName());
                    showStatus("Successfully unsubscribed from " + channel.getName(), false);
                    
                    if (button != null) {
                        button.removeActionListener(button.getActionListeners()[0]);
                        button.setText("Subscribe");
                        button.setBackground(new Color(40, 167, 69));
                        button.addActionListener(e -> subscribeToChannel(channel));
                        button.setEnabled(true);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Failed to unsubscribe from channel");
                    e.printStackTrace();
                    showStatus("Failed to unsubscribe from " + channel.getName(), true);
                    
                    if (button != null) {
                        button.setText("Unsubscribe");
                        button.setEnabled(true);
                    }
                }
            }
        };
        
        worker.execute();
    }
    
    private void openChannelHome(Channel channel) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return channelService.isUserSubscribed(currentUser.getId(), channel.getId());
            }
            
            @Override
            protected void done() {
                try {
                    boolean isSubscribed = get();
                    
                    if (isSubscribed) {
                        ChannelHomeFrame channelHomeFrame = new ChannelHomeFrame(
                            currentUser, channel, channelService, messageService);
                        channelHomeFrame.setVisible(true);
                    } else {
                        showStatus("Please subscribe to " + channel.getName() + " to access its content", true);
                    }
                    
                } catch (Exception e) {
                    System.err.println("Failed to open channel home");
                    e.printStackTrace();
                    showStatus("Failed to open channel: " + e.getMessage(), true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(40, 167, 69));
        
        Timer timer = new Timer(5000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}
