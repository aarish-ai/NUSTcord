package com.nustcord.dao;

/**
 * UserDAO.java
 * Purpose: Data Access Object for all user-account database operations.
 * Key Responsibilities:
 *  - Register new user accounts (INSERT)
 *  - Look up user records by username or by primary key
 *  - All queries use PreparedStatements to prevent SQL injection
 * Created: 2026-05-12
 */

import com.nustcord.model.User;
import com.nustcord.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides CRUD-style database access for the "users" table.
 * Each method opens and closes its own connection using try-with-resources
 * so connections are never leaked even if an exception is thrown.
 */
public class UserDAO {

    /**
     * Inserts a new user record into the "users" table.
     * The password stored in the User object must already be BCrypt-hashed
     * before this method is called (hashing is done in AuthService).
     *
     * @param user A populated User object with username, email, and hashed password.
     * @return true if the INSERT succeeded (one row affected), false otherwise.
     * @throws SQLException if a unique-key violation or connection error occurs.
     */
    public boolean registerUser(User user) throws SQLException {
        // IMPORTANT: We use PreparedStatement here to prevent SQL injection.
        // Never concatenate user input directly into SQL strings.
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        // try-with-resources automatically closes Connection and PreparedStatement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind parameters in order: username, email, hashed password
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());

            // executeUpdate returns the number of rows affected; > 0 means success
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves a single User record by username.
     * Used during login to load the stored password hash for BCrypt comparison.
     *
     * @param username The username to search for (case-sensitive, as stored in DB).
     * @return A populated User object if found, or null if no match exists.
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind the username parameter
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                // rs.next() advances to the first (and only expected) row
                if (rs.next()) {
                    // Map each column to the corresponding User field
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            // Log the error but don't rethrow – callers handle null returns gracefully
            e.printStackTrace();
        }

        // Return null to signal "user not found"
        return null;
    }

    /**
     * Retrieves a single User record by their primary key (integer ID).
     * Used when we have a userId from the session and need the full User object.
     *
     * @param id The user's integer primary key.
     * @return A populated User object if found, or null if the ID doesn't exist.
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind the integer ID parameter
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
