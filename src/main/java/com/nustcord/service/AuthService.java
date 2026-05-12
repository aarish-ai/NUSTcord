package com.nustcord.service;

/**
 * AuthService.java
 * Purpose: Business logic for user authentication (login and registration).
 * Key Responsibilities:
 *  - Validate registration inputs (length, uniqueness)
 *  - Hash plain-text passwords with BCrypt before storage
 *  - Verify BCrypt hashes during login without ever storing plain-text passwords
 *  - Throw AuthException with user-readable messages on any validation failure
 * Created: 2026-05-12
 */

import com.nustcord.dao.UserDAO;
import com.nustcord.exception.AuthException;
import com.nustcord.model.User;
import com.nustcord.util.PasswordUtil;

/**
 * Provides authentication operations for servlets.
 * Acts as the middle layer between LoginServlet/RegisterServlet and UserDAO,
 * enforcing business rules before hitting the database.
 */
public class AuthService {

    // UserDAO handles all database interaction; AuthService only applies rules
    private final UserDAO userDAO = new UserDAO();

    /**
     * Registers a new user account.
     * Validation order:
     *  1. Username and password must be non-null and non-empty
     *  2. Password must be at least 6 characters
     *  3. Username must not already exist in the database
     * On success, BCrypt-hashes the password and delegates to UserDAO.
     *
     * @param username          The desired login name (must be unique).
     * @param email             The user's email address.
     * @param plainTextPassword The raw password from the form (will be hashed).
     * @return true if the user was successfully inserted into the database.
     * @throws AuthException if validation fails or a duplicate username/email exists.
     */
    public boolean register(String username, String email, String plainTextPassword) throws AuthException {

        // Validate that username and password are present and meet minimum length requirements
        if (username == null || username.trim().isEmpty() ||
            plainTextPassword == null || plainTextPassword.length() < 6) {
            throw new AuthException("Invalid username or password must be at least 6 characters.");
        }

        // Check for duplicate username before attempting INSERT to give a clear error message
        if (userDAO.getUserByUsername(username) != null) {
            throw new AuthException("Username already exists.");
        }

        // IMPORTANT: Hash the plain-text password with BCrypt before storing.
        // BCrypt is a one-way function; the original password cannot be recovered.
        String hashedPassword = PasswordUtil.hashPassword(plainTextPassword);

        // Build the User model with the hashed password (id=0 because DB auto-increments it)
        User newUser = new User(0, username, email, hashedPassword, null);

        try {
            return userDAO.registerUser(newUser);

        } catch (java.sql.SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());

            // Distinguish duplicate-key constraint violations from other SQL errors
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique") ||
                e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new AuthException("Username or Email already taken.");
            }
            throw new AuthException("A database error occurred: " + e.getMessage());
        }
    }

    /**
     * Authenticates a user by verifying their password against the stored BCrypt hash.
     * Does NOT create or modify sessions – that responsibility belongs to LoginServlet.
     *
     * @param username          The login name provided by the user.
     * @param plainTextPassword The password provided by the user (plain text from form).
     * @return The authenticated User object (contains userId for session storage).
     * @throws AuthException if the username doesn't exist or the password doesn't match.
     */
    public User login(String username, String plainTextPassword) throws AuthException {

        // Look up the user record from the database
        User user = userDAO.getUserByUsername(username);

        // Treat "user not found" and "wrong password" identically to prevent username enumeration
        if (user == null) {
            throw new AuthException("Invalid username or password.");
        }

        // Verify plain text password against the stored secure hash using BCrypt
        if (!PasswordUtil.checkPassword(plainTextPassword, user.getPasswordHash())) {
            throw new AuthException("Invalid username or password.");
        }

        // Credentials are valid – return the full User object to the calling servlet
        return user;
    }
}
