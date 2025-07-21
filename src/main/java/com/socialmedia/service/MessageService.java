package main.java.com.socialmedia.service;

import main.java.com.socialmedia.dao.MessageDAO;
import com.socialmedia.exception.DatabaseException;
import main.java.com.socialmedia.exception.MessageTooLongException;
import main.java.com.socialmedia.model.Message;
import main.java.com.socialmedia.observer.MessagePublisher;

import java.util.List;

/**
 * Service class for message-related operations.
 * Handles message creation, validation, and pub/sub notifications.
 */
public class MessageService {
    private final MessageDAO messageDAO;
    private final MessagePublisher messagePublisher;
    
    public MessageService() {
        this.messageDAO = new MessageDAO();
        this.messagePublisher = new MessagePublisher();
    }
    
    /**
     * Posts a new message to a channel.
     * @param channelId Channel ID
     * @param userId User ID
     * @param content Message content
     * @return Created message
     * @throws MessageTooLongException if message exceeds character limit
     * @throws DatabaseException if database operation fails
     */
    public Message postMessage(int channelId, int userId, String content) throws MessageTooLongException, DatabaseException {
        // Validate content
        if (content == null) {
            throw new IllegalArgumentException("Message content cannot be null");
        }
        
        String trimmedContent = content.trim();
        if (trimmedContent.isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        
        if (trimmedContent.length() > Message.MAX_CONTENT_LENGTH) {
            throw new MessageTooLongException(trimmedContent.length(), Message.MAX_CONTENT_LENGTH);
        }
        
        // Create message
        Message message = new Message(channelId, userId, trimmedContent);
        Message createdMessage = messageDAO.createMessage(message);
        
        // Notify subscribers
        messagePublisher.publishMessage(createdMessage);
        
        System.out.println("Message posted successfully: ID=" + createdMessage.getId() +
                           ", Channel=" + channelId + ", User=" + userId);
        
        return createdMessage;
    }
    
    /**
     * Gets all messages for a channel.
     * @param channelId Channel ID
     * @return List of messages
     * @throws DatabaseException if database operation fails
     */
    public List<Message> getMessagesForChannel(int channelId) throws DatabaseException {
        return messageDAO.getMessagesForChannel(channelId);
    }
    
    /**
     * Gets all messages for channels that a user is subscribed to.
     * @param userId User ID
     * @return List of messages from subscribed channels
     * @throws DatabaseException if database operation fails
     */
    public List<Message> getMessagesForUser(int userId) throws DatabaseException {
        return messageDAO.getMessagesForUser(userId);
    }
    
    /**
     * Gets the message publisher for subscription management.
     * @return MessagePublisher instance
     */
    public MessagePublisher getMessagePublisher() {
        return messagePublisher;
    }
}
