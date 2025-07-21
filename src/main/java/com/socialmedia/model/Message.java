package main.java.com.socialmedia.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model class representing a message in the social media application.
 * Messages belong to channels and are posted by users.
 */
public class Message {
    public static final int MAX_CONTENT_LENGTH = 200;
    
    private int id;
    private int channelId;
    private int userId;
    private String content;
    private LocalDateTime createdAt;
    private String userEmail; // For display purposes
    
    /**
     * Default constructor for Message.
     */
    public Message() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with essential fields.
     * @param channelId Channel where message is posted
     * @param userId User who posted the message
     * @param content Message content
     */
    public Message(int channelId, int userId, String content) {
        this();
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
    }
    
    /**
     * Full constructor for Message.
     * @param id Message's unique identifier
     * @param channelId Channel where message is posted
     * @param userId User who posted the message
     * @param content Message content
     * @param createdAt Message creation timestamp
     */
    public Message(int id, int channelId, int userId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getChannelId() { return channelId; }
    public void setChannelId(int channelId) { this.channelId = channelId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    /**
     * Validates message content length.
     * @return true if content length is valid
     */
    public boolean isContentValid() {
        return content != null && content.length() <= MAX_CONTENT_LENGTH;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", channelId=" + channelId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}