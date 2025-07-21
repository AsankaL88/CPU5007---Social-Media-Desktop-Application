package main.java.com.socialmedia.exception;

/**
 * Exception thrown when message content exceeds the maximum allowed length.
 */
public class MessageTooLongException extends Exception {
    private final int maxLength;
    private final int actualLength;
    
    public MessageTooLongException(int actualLength, int maxLength) {
        super("Your content should contain fewer than " + maxLength + " characters. Current length: " + actualLength);
        this.maxLength = maxLength;
        this.actualLength = actualLength;
    }
    
    public int getMaxLength() { return maxLength; }
    public int getActualLength() { return actualLength; }
}