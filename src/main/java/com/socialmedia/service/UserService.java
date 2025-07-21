package main.java.com.socialmedia.service;

import main.java.com.socialmedia.dao.UserDAO;
import com.socialmedia.exception.DatabaseException;
import com.socialmedia.exception.InvalidCredentialsException;
import main.java.com.socialmedia.exception.UserAlreadyExistsException;
import main.java.com.socialmedia.model.User;



import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * Service class for user-related operations.
 * Handles user authentication, registration, and password hashing using PBKDF2.
 */
public class UserService {
    private final UserDAO userDAO;
    private static final int SALT_LENGTH = 16; // bytes
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Registers a new user.
     */
    public User registerUser(String email, String password) throws UserAlreadyExistsException, DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        // Store combined salt and hash as: salt:hash
        String storedPassword = salt + ":" + hashedPassword;

        User user = new User(email.trim().toLowerCase(), storedPassword);
        User createdUser = userDAO.createUser(user);

        System.out.println("User registered successfully: " + email);
        return createdUser;
    }

    /**
     * Authenticates a user.
     */
    public User authenticateUser(String email, String password) throws InvalidCredentialsException, DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidCredentialsException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidCredentialsException("Password cannot be empty");
        }

        Optional<User> userOptional = userDAO.findUserByEmail(email.trim().toLowerCase());
        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userOptional.get();
        String storedPassword = user.getPassword(); // salt:hash

        String[] parts = storedPassword.split(":");
        if (parts.length != 2) {
            throw new InvalidCredentialsException("Invalid stored password format");
        }

        String salt = parts[0];
        String hash = parts[1];

        if (!verifyPassword(password, salt, hash)) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        System.out.println("User authenticated successfully: " + email);
        return user;
    }

    /**
     * Checks if user exists.
     */
    public boolean userExists(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userDAO.existsByEmail(email.trim().toLowerCase());
    }

    /**
     * Finds user by ID.
     */
    public Optional<User> findUserById(int id) throws DatabaseException {
        return userDAO.findUserById(id);
    }

    // Helper methods for PBKDF2 hashing and verification

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing a password: " + e.getMessage(), e);
        }
    }

    private boolean verifyPassword(String password, String saltBase64, String expectedHashBase64) {
        String hashOfInput = hashPassword(password, saltBase64);
        return hashOfInput.equals(expectedHashBase64);
    }
}
