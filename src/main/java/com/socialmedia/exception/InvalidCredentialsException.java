package com.socialmedia.exception;

/**
 * Exception thrown when user provides invalid login credentials.
 */
public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException() {
        super("Invalid email or password provided");
    }
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
}