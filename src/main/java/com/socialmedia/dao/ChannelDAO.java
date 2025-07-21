package main.java.com.socialmedia.dao;

import main.java.com.socialmedia.database.DatabaseManager;
import com.socialmedia.exception.DatabaseException;
import  main.java.com.socialmedia.model.Channel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Channel operations.
 * Handles all database operations related to channels.
 */
public class ChannelDAO {
    private final DatabaseManager databaseManager;

    public ChannelDAO() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Creates a new channel in the database.
     * @param channel Channel to create
     * @return Created channel with ID
     * @throws DatabaseException if database operation fails
     */
    public Channel createChannel(Channel channel) throws DatabaseException {
        String sql = "INSERT INTO channels (name, description) VALUES (?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, channel.getName());
            stmt.setString(2, channel.getDescription());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create channel, no rows affected");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    channel.setId(rs.getInt(1));
                }
            }

            System.out.println("Channel created successfully with ID: " + channel.getId());
            return channel;

        } catch (SQLException e) {
            System.err.println("Failed to create channel");
            e.printStackTrace();
            throw new DatabaseException("Failed to create channel", e);
        }
    }

    /**
     * Finds a channel by name.
     * @param name Channel name
     * @return Optional containing channel if found
     * @throws DatabaseException if database operation fails
     */
    public Optional<Channel> findChannelByName(String name) throws DatabaseException {
        String sql = "SELECT id, name, description, created_at FROM channels WHERE name = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Channel channel = new Channel();
                    channel.setId(rs.getInt("id"));
                    channel.setName(rs.getString("name"));
                    channel.setDescription(rs.getString("description"));
                    channel.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return Optional.of(channel);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to find channel by name");
            e.printStackTrace();
            throw new DatabaseException("Failed to find channel by name", e);
        }

        return Optional.empty();
    }

    /**
     * Gets all channels.
     * @return List of all channels
     * @throws DatabaseException if database operation fails
     */
    public List<Channel> getAllChannels() throws DatabaseException {
        String sql = "SELECT id, name, description, created_at FROM channels ORDER BY name";
        List<Channel> channels = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Channel channel = new Channel();
                channel.setId(rs.getInt("id"));
                channel.setName(rs.getString("name"));
                channel.setDescription(rs.getString("description"));
                channel.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                channels.add(channel);
            }

        } catch (SQLException e) {
            System.err.println("Failed to get all channels");
            e.printStackTrace();
            throw new DatabaseException("Failed to get all channels", e);
        }

        return channels;
    }

    /**
     * Subscribes a user to a channel.
     * @param userId User ID
     * @param channelId Channel ID
     * @throws DatabaseException if database operation fails
     */
    public void subscribeUser(int userId, int channelId) throws DatabaseException {
        String sql = "INSERT OR IGNORE INTO subscriptions (user_id, channel_id) VALUES (?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, channelId);

            stmt.executeUpdate();
            System.out.println("User " + userId + " subscribed to channel " + channelId);

        } catch (SQLException e) {
            System.err.println("Failed to subscribe user to channel");
            e.printStackTrace();
            throw new DatabaseException("Failed to subscribe user to channel", e);
        }
    }

    /**
     * Unsubscribes a user from a channel.
     * @param userId User ID
     * @param channelId Channel ID
     * @throws DatabaseException if database operation fails
     */
    public void unsubscribeUser(int userId, int channelId) throws DatabaseException {
        String sql = "DELETE FROM subscriptions WHERE user_id = ? AND channel_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, channelId);

            stmt.executeUpdate();
            System.out.println("User " + userId + " unsubscribed from channel " + channelId);

        } catch (SQLException e) {
            System.err.println("Failed to unsubscribe user from channel");
            e.printStackTrace();
            throw new DatabaseException("Failed to unsubscribe user from channel", e);
        }
    }

    /**
     * Checks if a user is subscribed to a channel.
     * @param userId User ID
     * @param channelId Channel ID
     * @return true if user is subscribed
     * @throws DatabaseException if database operation fails
     */
    public boolean isUserSubscribed(int userId, int channelId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM subscriptions WHERE user_id = ? AND channel_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, channelId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to check subscription status");
            e.printStackTrace();
            throw new DatabaseException("Failed to check subscription status", e);
        }

        return false;
    }
}
