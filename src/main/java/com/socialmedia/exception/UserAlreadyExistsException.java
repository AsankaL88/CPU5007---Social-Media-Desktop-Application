package main.java.com.socialmedia.exception;

/**
 * Exception thrown when attempting to create a user with an email that already exists.
 */
public class UserAlreadyExistsException extends Exception {
    private final String email;
    
    public UserAlreadyExistsException(String email) {
        super("The email address entered has already been registered: " + email);
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
}