package main.java.com.socialmedia.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model class representing a user in the social media application.
 * Implements user authentication and profile information.
 */
public class User {
    private int id;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor for User.
     */
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with email and password.
     * @param email User's email address
     * @param password User's password (should be hashed)
     */
    public User(String email, String password) {
        this();
        this.email = email;
        this.password = password;
    }
    
    /**
     * Full constructor for User.
     * @param id User's unique identifier
     * @param email User's email address
     * @param password User's password (should be hashed)
     * @param createdAt User's account creation timestamp
     */
    public User(int id, String email, String password, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}