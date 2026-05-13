package com.nustcord.model;

import java.sql.Timestamp;

public class Server {
    private int id;
    private String name;
    private int ownerId;
    private Timestamp createdAt;
    /** BCrypt hash of the join password; null means the server is open (no password). */
    private String passwordHash;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /** Returns true when this server requires a password to join. */
    public boolean isPasswordProtected() { return passwordHash != null && !passwordHash.isEmpty(); }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
