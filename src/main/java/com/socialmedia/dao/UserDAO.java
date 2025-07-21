package main.java.com.socialmedia.dao;

import main.java.com.socialmedia.database.DatabaseManager;
import com.socialmedia.exception.DatabaseException;
import main.java.com.socialmedia.exception.UserAlreadyExistsException;
import main.java.com.socialmedia.model.User;

import java.sql.*;
import java.util.Optional;

/**
 * Data Access Object for User operations.
 * Handles all database operations related to users.
 */
public class UserDAO {
    private final DatabaseManager databaseManager;

    public UserDAO() {
        this.databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Creates a new user in the database.
     * @param user User to create
     * @return Created user with ID
     * @throws UserAlreadyExistsException if email already exists
     * @throws DatabaseException if database operation fails
     */
    public User createUser(User user) throws UserAlreadyExistsException, DatabaseException {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DatabaseException("Failed to create user, no rows affected");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }

            System.out.println("User created successfully with ID: " + user.getId());
            return user;

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new UserAlreadyExistsException(user.getEmail());
            }
            System.err.println("Failed to create user");
            e.printStackTrace();
            throw new DatabaseException("Failed to create user", e);
        }
    }

    /**
     * Finds a user by email.
     * @param email User's email
     * @return Optional containing user if found
     * @throws DatabaseException if database operation fails
     */
    public Optional<User> findUserByEmail(String email) throws DatabaseException {
        String sql = "SELECT id, email, password, created_at FROM users WHERE email = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to find user by email");
            e.printStackTrace();
            throw new DatabaseException("Failed to find user by email", e);
        }

        return Optional.empty();
    }

    /**
     * Finds a user by ID.
     * @param id User's ID
     * @return Optional containing user if found
     * @throws DatabaseException if database operation fails
     */
    public Optional<User> findUserById(int id) throws DatabaseException {
        String sql = "SELECT id, email, password, created_at FROM users WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to find user by ID");
            e.printStackTrace();
            throw new DatabaseException("Failed to find user by ID", e);
        }

        return Optional.empty();
    }

    /**
     * Checks if a user exists by email.
     * @param email User's email
     * @return true if user exists
     * @throws DatabaseException if database operation fails
     */
    public boolean existsByEmail(String email) throws DatabaseException {
        return findUserByEmail(email).isPresent();
    }
}
