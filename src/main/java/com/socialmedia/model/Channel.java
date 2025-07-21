package main.java.com.socialmedia.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model class representing a channel in the social media application.
 * Channels contain messages and have subscribers.
 */
public class Channel {
    private int id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor for Channel.
     */
    public Channel() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with name and description.
     * @param name Channel name
     * @param description Channel description
     */
    public Channel(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    /**
     * Full constructor for Channel.
     * @param id Channel's unique identifier
     * @param name Channel name
     * @param description Channel description
     * @param createdAt Channel creation timestamp
     */
    public Channel(int id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return id == channel.id && Objects.equals(name, channel.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}