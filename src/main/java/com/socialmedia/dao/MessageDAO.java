package main.java.com.socialmedia.dao;

import main.java.com.socialmedia.database.DatabaseManager;
import com.socialmedia.exception.DatabaseException;
import main.java.com.socialmedia.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Message operations.
 * Handles all database operations related to messages.
 */
public class MessageDAO {
    private final DatabaseManager databaseManager;

    public MessageDAO() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Creates a new message in the database.
     * @param message Message to create
     * @return Created message with ID
     * @throws DatabaseException if database operation fails
     */
    public Message createMessage(Message message) throws DatabaseException {
        String sql = "INSERT INTO messages (channel_id, user_id, content) VALUES (?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, message.getChannelId());
            stmt.setInt(2, message.getUserId());
            stmt.setString(3, message.getContent());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create message, no rows affected");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
            }

            System.out.println("Message created successfully with ID: " + message.getId());
            return message;

        } catch (SQLException e) {
            System.err.println("Failed to create message");
            e.printStackTrace();
            throw new DatabaseException("Failed to create message", e);
        }
    }

    /**
     * Gets all messages for a channel.
     * @param channelId Channel ID
     * @return List of messages for the channel
     * @throws DatabaseException if database operation fails
     */
    public List<Message> getMessagesForChannel(int channelId) throws DatabaseException {
        String sql = """
            SELECT m.id, m.channel_id, m.user_id, m.content, m.created_at, u.email
            FROM messages m
            JOIN users u ON m.user_id = u.id
            WHERE m.channel_id = ?
            ORDER BY m.created_at DESC
        """;

        List<Message> messages = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, channelId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getInt("id"));
                    message.setChannelId(rs.getInt("channel_id"));
                    message.setUserId(rs.getInt("user_id"));
                    message.setContent(rs.getString("content"));
                    message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    message.setUserEmail(rs.getString("email"));
                    messages.add(message);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to get messages for channel");
            e.printStackTrace();
            throw new DatabaseException("Failed to get messages for channel", e);
        }

        return messages;
    }

    /**
     * Gets all messages for channels that a user is subscribed to.
     * @param userId User ID
     * @return List of messages from subscribed channels
     * @throws DatabaseException if database operation fails
     */
    public List<Message> getMessagesForUser(int userId) throws DatabaseException {
        String sql = """
            SELECT m.id, m.channel_id, m.user_id, m.content, m.created_at, u.email
            FROM messages m
            JOIN users u ON m.user_id = u.id
            JOIN subscriptions s ON m.channel_id = s.channel_id
            WHERE s.user_id = ?
            ORDER BY m.created_at DESC
        """;

        List<Message> messages = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getInt("id"));
                    message.setChannelId(rs.getInt("channel_id"));
                    message.setUserId(rs.getInt("user_id"));
                    message.setContent(rs.getString("content"));
                    message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    message.setUserEmail(rs.getString("email"));
                    messages.add(message);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to get messages for user");
            e.printStackTrace();
            throw new DatabaseException("Failed to get messages for user", e);
        }

        return messages;
    }
}
