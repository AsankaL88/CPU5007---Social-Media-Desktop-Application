package main.java.com.socialmedia.gui;

import main.java.com.socialmedia.exception.MessageTooLongException;
import main.java.com.socialmedia.model.Channel;
import main.java.com.socialmedia.model.Message;
import main.java.com.socialmedia.model.User;
import main.java.com.socialmedia.observer.MessageSubscriber;
import main.java.com.socialmedia.service.ChannelService;
import main.java.com.socialmedia.service.MessageService;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChannelHomeFrame extends JFrame implements MessageSubscriber {

    private final User currentUser;
    private final Channel channel;
    private final ChannelService channelService;
    private final MessageService messageService;

    private JTextPane messagesPane;
    private JTextArea postArea;
    private JButton postButton;
    private JLabel statusLabel;
    private JLabel charCountLabel;
    private JButton backButton;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public ChannelHomeFrame(User currentUser, Channel channel, ChannelService channelService, MessageService messageService) {
        this.currentUser = currentUser;
        this.channel = channel;
        this.channelService = channelService;
        this.messageService = messageService;

        messageService.getMessagePublisher().subscribe(this);

        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadMessages();

        setTitle("Social Media Application - " + channel.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        messagesPane = new JTextPane();
        messagesPane.setEditable(false);
        messagesPane.setBackground(Color.WHITE);
        messagesPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        postArea = new JTextArea(3, 40);
        postArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        postArea.setLineWrap(true);
        postArea.setWrapStyleWord(true);
        postArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        postButton = new JButton("Post");
        postButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        postButton.setBackground(new Color(0, 123, 255));
        postButton.setForeground(Color.WHITE);
        postButton.setPreferredSize(new Dimension(80, 35));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);

        charCountLabel = new JLabel("0/" + Message.MAX_CONTENT_LENGTH);
        charCountLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        charCountLabel.setForeground(Color.GRAY);

        // Fixed back button initialization with better visibility
        backButton = new JButton("← Back");
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        // Create back button with explicit visibility settings
        backButton = new JButton("← Back");
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.setMinimumSize(new Dimension(100, 40));
        backButton.setMaximumSize(new Dimension(100, 40));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.setVisible(true);
        
        // Add mouse hover effect
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(90, 99, 107));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(108, 117, 125));
            }
        });
        
        // Add hover effect for better user experience
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(90, 99, 107));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(108, 117, 125));
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create header panel with proper layout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setPreferredSize(new Dimension(700, 70));

        // Create back button panel with proper sizing
        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        backPanel.setBackground(new Color(248, 249, 250));
        backPanel.setPreferredSize(new Dimension(120, 50));
        backPanel.setMinimumSize(new Dimension(120, 50));
        backPanel.add(backButton);

        // Create title panel
        JLabel titleLabel = new JLabel(channel.getName());
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        JLabel descLabel = new JLabel(channel.getDescription());
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(248, 249, 250));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(descLabel, BorderLayout.CENTER);

        // Add components to header
        headerPanel.add(backPanel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        JScrollPane messagesScroll = new JScrollPane(messagesPane);
        messagesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messagesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messagesScroll.setBorder(BorderFactory.createTitledBorder("Messages"));

        JPanel postPanel = new JPanel(new BorderLayout());
        postPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Post a Message"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        postPanel.setBackground(Color.WHITE);

        JScrollPane postScroll = new JScrollPane(postArea);
        postScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        postScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel postControlPanel = new JPanel(new BorderLayout());
        postControlPanel.setBackground(Color.WHITE);
        postControlPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel charCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        charCountPanel.setBackground(Color.WHITE);
        charCountPanel.add(charCountLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(postButton);

        postControlPanel.add(charCountPanel, BorderLayout.WEST);
        postControlPanel.add(buttonPanel, BorderLayout.EAST);

        postPanel.add(postScroll, BorderLayout.CENTER);
        postPanel.add(postControlPanel, BorderLayout.SOUTH);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        statusPanel.setBackground(new Color(248, 249, 250));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);
        add(messagesScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(postPanel, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        postButton.addActionListener(new PostMessageActionListener());

        postArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateCharCount();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateCharCount();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateCharCount();
            }
        });

        postArea.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke("ctrl ENTER"), "post");
        postArea.getActionMap().put("post", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                postButton.doClick();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                messageService.getMessagePublisher().unsubscribe(ChannelHomeFrame.this);
            }
        });

        backButton.addActionListener(e -> {
            messageService.getMessagePublisher().unsubscribe(ChannelHomeFrame.this);
            ChannelsFrame channelsFrame = new ChannelsFrame(currentUser, channelService, messageService);
            channelsFrame.setVisible(true);
            dispose();
        });
    }

    private void updateCharCount() {
        int length = postArea.getText().length();
        charCountLabel.setText(length + "/" + Message.MAX_CONTENT_LENGTH);
        if (length > Message.MAX_CONTENT_LENGTH) {
            charCountLabel.setForeground(Color.RED);
        } else if (length > Message.MAX_CONTENT_LENGTH * 0.9) {
            charCountLabel.setForeground(Color.ORANGE);
        } else {
            charCountLabel.setForeground(Color.GRAY);
        }
    }

    private void loadMessages() {
        SwingWorker<List<Message>, Void> worker = new SwingWorker<>() {
            protected List<Message> doInBackground() throws Exception {
                return messageService.getMessagesForChannel(channel.getId());
            }

            protected void done() {
                try {
                    List<Message> messages = get();
                    displayMessages(messages);
                } catch (Exception e) {
                    showStatus("Failed to load messages: " + e.getMessage(), true);
                }
            }
        };
        worker.execute();
    }

    private void displayMessages(List<Message> messages) {
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = messagesPane.getStyledDocument();
            try {
                doc.remove(0, doc.getLength());
                if (messages.isEmpty()) {
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    StyleConstants.setForeground(attrs, Color.GRAY);
                    StyleConstants.setItalic(attrs, true);
                    doc.insertString(0, "No messages yet. Be the first to post!", attrs);
                } else {
                    for (int i = messages.size() - 1; i >= 0; i--) {
                        appendMessage(doc, messages.get(i));
                        if (i > 0) doc.insertString(doc.getLength(), "\n\n", null);
                    }
                }
                messagesPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                System.err.println("Failed to display messages: " + e.getMessage());
            }
        });
    }

    private void appendMessage(StyledDocument doc, Message message) throws BadLocationException {
        SimpleAttributeSet userStyle = new SimpleAttributeSet();
        StyleConstants.setBold(userStyle, true);
        StyleConstants.setForeground(userStyle, new Color(0, 123, 255));

        SimpleAttributeSet timeStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(timeStyle, Color.GRAY);
        StyleConstants.setFontSize(timeStyle, 11);

        SimpleAttributeSet messageStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(messageStyle, Color.BLACK);

        doc.insertString(doc.getLength(), message.getUserEmail(), userStyle);
        doc.insertString(doc.getLength(), " • ", timeStyle);
        doc.insertString(doc.getLength(), message.getCreatedAt().format(timeFormatter), timeStyle);
        doc.insertString(doc.getLength(), "\n", null);
        doc.insertString(doc.getLength(), message.getContent(), messageStyle);
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(40, 167, 69));
        Timer timer = new Timer(5000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message.getChannelId() == channel.getId()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    StyledDocument doc = messagesPane.getStyledDocument();
                    if (doc.getLength() > 0) {
                        doc.insertString(doc.getLength(), "\n\n", null);
                    }
                    appendMessage(doc, message);
                    messagesPane.setCaretPosition(doc.getLength());
                } catch (BadLocationException e) {
                    System.err.println("Failed to display new message: " + e.getMessage());
                }
            });
        }
    }

    private class PostMessageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String content = postArea.getText().trim();
            if (content.isEmpty()) {
                showStatus("Please enter a message", true);
                return;
            }

            postButton.setEnabled(false);
            postButton.setText("Posting...");

            SwingWorker<Message, Void> worker = new SwingWorker<>() {
                protected Message doInBackground() throws Exception {
                    return messageService.postMessage(channel.getId(), currentUser.getId(), content);
                }

                protected void done() {
                    try {
                        get();
                        postArea.setText("");
                        updateCharCount();
                        showStatus("Message posted successfully!", false);
                    } catch (Exception ex) {
                        Throwable cause = ex.getCause();
                        if (cause instanceof MessageTooLongException) {
                            showStatus(cause.getMessage(), true);
                        } else {
                            showStatus("Failed to post message: " + ex.getMessage(), true);
                        }
                    } finally {
                        postButton.setEnabled(true);
                        postButton.setText("Post");
                    }
                }
            };
            worker.execute();
        }
    }
}