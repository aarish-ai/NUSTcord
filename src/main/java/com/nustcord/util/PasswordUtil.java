package com.nustcord.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hash a plaintext password
    public static String hashPassword(String plainTextPassword) {
        // Generates a robust salt and hashes the password
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Check if the plaintext password matches the hashed string
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
