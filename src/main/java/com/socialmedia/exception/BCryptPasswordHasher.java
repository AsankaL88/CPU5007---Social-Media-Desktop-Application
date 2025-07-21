package main.java.com.socialmedia.exception;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing operations.
 * Provides secure password hashing and verification using PBKDF2.
 */
public class BCryptPasswordHasher {
    private static final int SALT_LENGTH = 16; // bytes
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits

    /**
     * Hashes a password using PBKDF2 with a generated salt.
     * Stored format: "salt:hash", Base64 encoded.
     * @param password Plain text password
     * @return Hashed password string in "salt:hash" format
     */
    public static String hashPassword(String password) {
        String salt = generateSalt();
        String hash = hashPasswordWithSalt(password, salt);
        return salt + ":" + hash;
    }

    /**
     * Verifies a password against a stored hash in "salt:hash" format.
     * @param password Plain text password
     * @param storedPassword Stored password hash with salt ("salt:hash")
     * @return true if password matches hash
     */
    public static boolean verifyPassword(String password, String storedPassword) {
        if (storedPassword == null || !storedPassword.contains(":")) {
            throw new IllegalArgumentException("Invalid stored password format");
        }
        String[] parts = storedPassword.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid stored password format");
        }
        String salt = parts[0];
        String hash = parts[1];
        String computedHash = hashPasswordWithSalt(password, salt);
        return computedHash.equals(hash);
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private static String hashPasswordWithSalt(String password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }
}
