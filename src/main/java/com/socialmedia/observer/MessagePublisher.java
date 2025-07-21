package main.java.com.socialmedia.observer;

import main.java.com.socialmedia.model.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Publisher in the Observer pattern for message broadcasting.
 * Notifies all subscribers when new messages are posted.
 */
public class MessagePublisher {
    private final List<MessageSubscriber> subscribers = new CopyOnWriteArrayList<>();
    
    /**
     * Adds a subscriber to receive message notifications.
     * @param subscriber Subscriber to add
     */
    public void subscribe(MessageSubscriber subscriber) {
        if (subscriber != null && !subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
            System.out.println("Subscriber added: " + subscriber.getClass().getSimpleName());
        }
    }
    
    /**
     * Removes a subscriber from message notifications.
     * @param subscriber Subscriber to remove
     */
    public void unsubscribe(MessageSubscriber subscriber) {
        if (subscriber != null) {
            subscribers.remove(subscriber);
            System.out.println("Subscriber removed: " + subscriber.getClass().getSimpleName());
        }
    }
    
    /**
     * Publishes a message to all subscribers.
     * @param message Message to publish
     */
    public void publishMessage(Message message) {
        if (message == null) {
            System.out.println("Warning: Attempted to publish null message");
            return;
        }
        
        System.out.println("Publishing message to " + subscribers.size() + " subscribers");
        
        // Notify all subscribers in parallel to avoid blocking
        subscribers.parallelStream().forEach(subscriber -> {
            try {
                subscriber.onMessageReceived(message);
            } catch (Exception e) {
                System.err.println("Error notifying subscriber: " + subscriber.getClass().getSimpleName());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Gets the number of current subscribers.
     * @return Number of subscribers
     */
    public int getSubscriberCount() {
        return subscribers.size();
    }
    
    /**
     * Removes all subscribers.
     */
    public void clearSubscribers() {
        subscribers.clear();
        System.out.println("All subscribers cleared");
    }
}
