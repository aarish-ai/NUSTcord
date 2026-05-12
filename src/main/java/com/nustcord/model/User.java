package com.nustcord.model;

/**
 * User.java
 * Purpose: Model class representing a registered NUSTcord user account.
 * Key Responsibilities:
 *  - Serve as a plain-old Java object (POJO) / data transfer object for user data
 *  - Hold all fields that directly correspond to columns in the "users" database table
 *  - Provide getters and setters for all fields (encapsulation)
 * Created: 2026-05-12
 */

import java.sql.Timestamp;

/**
 * Represents one row from the "users" table.
 * Instances are created by UserDAO after a successful database query.
 * This class is intentionally simple – business logic lives in the service layer.
 */
public class User {

    // Primary key – auto-incremented by the database
    private int id;

    // Unique login identifier chosen at registration
    private String username;

    // User's email address – must be unique in the database
    private String email;

    // BCrypt hash of the user's password; never store or transmit plain text
    private String passwordHash;

    // UTC timestamp set automatically by the database on INSERT
    private Timestamp createdAt;

    /**
     * No-argument constructor required by frameworks that instantiate
     * objects before setting individual properties.
     */
    public User() {}

    /**
     * Full constructor for convenient object creation in DAOs.
     *
     * @param id           The user's primary key.
     * @param username     The user's unique login name.
     * @param email        The user's email address.
     * @param passwordHash The BCrypt hash of the user's password.
     * @param createdAt    The registration timestamp from the database.
     */
    public User(int id, String username, String email, String passwordHash, Timestamp createdAt) {
        this.id           = id;
        this.username     = username;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.createdAt    = createdAt;
    }

    // ─── Getters and Setters ───────────────────────────────────────────────────

    /** @return the user's integer primary key */
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    /** @return the unique login username */
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /** @return the user's email address */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * Returns the BCrypt password hash stored in the database.
     * NEVER use this value for display; it is only for authentication comparison.
     *
     * @return the BCrypt password hash string
     */
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /** @return the UTC timestamp when this account was created */
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
