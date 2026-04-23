package com.nustcord.service;

import com.nustcord.dao.UserDAO;
import com.nustcord.exception.AuthException;
import com.nustcord.model.User;
import com.nustcord.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public boolean register(String username, String email, String plainTextPassword) throws AuthException {
        if (username == null || username.trim().isEmpty() || plainTextPassword == null || plainTextPassword.length() < 6) {
            throw new AuthException("Invalid username or password must be at least 6 characters.");
        }
        if (userDAO.getUserByUsername(username) != null) {
            throw new AuthException("Username already exists.");
        }

        // Securely hash password before storing
        String hashedPassword = PasswordUtil.hashPassword(plainTextPassword);
        User newUser = new User(0, username, email, hashedPassword, null);
        try {
            return userDAO.registerUser(newUser);
        } catch (java.sql.SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            // If it's a constraint violation
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique") || 
                e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                throw new AuthException("Username or Email already taken.");
            }
            throw new AuthException("A database error occurred: " + e.getMessage());
        }
    }

    public User login(String username, String plainTextPassword) throws AuthException {
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            throw new AuthException("Invalid username or password.");
        }
        
        // Verify plain text password against the stored secure hash
        if (!PasswordUtil.checkPassword(plainTextPassword, user.getPasswordHash())) {
            throw new AuthException("Invalid username or password.");
        }
        return user;
    }
}
