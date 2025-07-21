package main.java.com.socialmedia.service;

import main.java.com.socialmedia.dao.ChannelDAO;
import com.socialmedia.exception.DatabaseException;
import main.java.com.socialmedia.model.Channel;

import java.util.List;
import java.util.Optional;

/**
 * Service class for channel-related operations.
 * Handles channel management and subscription operations.
 */
public class ChannelService {
    private final ChannelDAO channelDAO;
    
    public ChannelService() {
        this.channelDAO = new ChannelDAO();
    }
    
    /**
     * Creates the default channel if it doesn't exist.
     * @throws DatabaseException if database operation fails
     */
    public void createDefaultChannel() throws DatabaseException {
        String defaultChannelName = "DiscountNews";
        String defaultChannelDescription = "Latest discount news and offers";
        
        Optional<Channel> existingChannel = channelDAO.findChannelByName(defaultChannelName);
        if (existingChannel.isEmpty()) {
            Channel channel = new Channel(defaultChannelName, defaultChannelDescription);
            channelDAO.createChannel(channel);
            System.out.println("Default channel created: " + defaultChannelName);
        }
    }
    
    /**
     * Gets all available channels.
     * @return List of all channels
     * @throws DatabaseException if database operation fails
     */
    public List<Channel> getAllChannels() throws DatabaseException {
        return channelDAO.getAllChannels();
    }
    
    /**
     * Finds a channel by name.
     * @param name Channel name
     * @return Optional containing channel if found
     * @throws DatabaseException if database operation fails
     */
    public Optional<Channel> findChannelByName(String name) throws DatabaseException {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return channelDAO.findChannelByName(name.trim());
    }
    
    /**
     * Subscribes a user to a channel.
     * @param userId User ID
     * @param channelId Channel ID
     * @throws DatabaseException if database operation fails
     */
    public void subscribeUser(int userId, int channelId) throws DatabaseException {
        channelDAO.subscribeUser(userId, channelId);
        System.out.println("User " + userId + " subscribed to channel " + channelId);
    }
    
    /**
     * Unsubscribes a user from a channel.
     * @param userId User ID
     * @param channelId Channel ID
     * @throws DatabaseException if database operation fails
     */
    public void unsubscribeUser(int userId, int channelId) throws DatabaseException {
        channelDAO.unsubscribeUser(userId, channelId);
        System.out.println("User " + userId + " unsubscribed from channel " + channelId);
    }
    
    /**
     * Checks if a user is subscribed to a channel.
     * @param userId User ID
     * @param channelId Channel ID
     * @return true if user is subscribed
     * @throws DatabaseException if database operation fails
     */
    public boolean isUserSubscribed(int userId, int channelId) throws DatabaseException {
        return channelDAO.isUserSubscribed(userId, channelId);
    }
    
    /**
     * Creates a new channel.
     * @param name Channel name
     * @param description Channel description
     * @return Created channel
     * @throws DatabaseException if database operation fails
     */
    public Channel createChannel(String name, String description) throws DatabaseException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name cannot be empty");
        }
        
        Channel channel = new Channel(name.trim(), description != null ? description.trim() : "");
        return channelDAO.createChannel(channel);
    }
}
