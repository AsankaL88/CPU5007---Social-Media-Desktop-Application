package main.java.com.socialmedia.database;

import com.socialmedia.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class for managing database connections and initialization.
 * Handles SQLite database setup and provides connection pooling.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:sqlite:social_media.db";

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found");
            e.printStackTrace();
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    /**
     * Gets the singleton instance of DatabaseManager.
     * @return DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Gets a database connection.
     * @return Database connection
     * @throws DatabaseException if connection fails
     */
    public Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Failed to get database connection");
            e.printStackTrace();
            throw new DatabaseException("Failed to get database connection", e);
        }
    }

    /**
     * Initializes the database with required tables.
     * @throws DatabaseException if initialization fails
     */
    public void initializeDatabase() throws DatabaseException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            stmt.execute(createUsersTable);

            // Create channels table
            String createChannelsTable = """
                CREATE TABLE IF NOT EXISTS channels (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE NOT NULL,
                    description TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            stmt.execute(createChannelsTable);

            // Create messages table
            String createMessagesTable = """
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    channel_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    content TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (channel_id) REFERENCES channels(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """;
            stmt.execute(createMessagesTable);

            // Create subscriptions table
            String createSubscriptionsTable = """
                CREATE TABLE IF NOT EXISTS subscriptions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    channel_id INTEGER NOT NULL,
                    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (channel_id) REFERENCES channels(id),
                    UNIQUE(user_id, channel_id)
                )
            """;
            stmt.execute(createSubscriptionsTable);

            System.out.println("Database initialized successfully");

        } catch (SQLException e) {
            System.err.println("Failed to initialize database");
            e.printStackTrace();
            throw new DatabaseException("Failed to initialize database", e);
        }
    }
}
