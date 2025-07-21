package main.java.com.socialmedia.observer;

import main.java.com.socialmedia.model.Message;

/**
 * Interface for message subscribers in the Observer pattern.
 * Implementations receive notifications when new messages are posted.
 */
public interface MessageSubscriber {
    /**
     * Called when a new message is received.
     * @param message The received message
     */
    void onMessageReceived(Message message);
}