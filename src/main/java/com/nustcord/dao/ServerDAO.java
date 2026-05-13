package com.nustcord.dao;

/**
 * ServerDAO.java
 * Purpose: Data Access Object for server-related database operations.
 * Key Responsibilities:
 *  - Create new servers (INSERT with generated-key retrieval, including optional password_hash)
 *  - List servers a specific user has joined (JOIN query)
 *  - List all servers in the system (for server discovery page)
 *  - Retrieve a single server by ID (for join-password validation)
 *  - Verify a plain-text password against the stored BCrypt hash
 * Created: 2026-05-12
 * Updated: 2026-05-13 — added server password protection
 */

import com.nustcord.model.Server;
import com.nustcord.util.DBConnection;
import com.nustcord.util.PasswordUtil;
import java.sql.*;
import java.util.*;

/**
 * Manages persistence for the "servers" table.
 * Works in conjunction with UserServerMapDAO to track server memberships.
 */
public class ServerDAO {

    /**
     * Inserts a new server record and writes the auto-generated primary key
     * back onto the Server model object so callers can use it immediately
     * (e.g., to create the default channel and map the owner).
     * If the Server has a non-null passwordHash, it is stored as-is (already BCrypt-hashed by the servlet).
     *
     * @param server A Server object with name, owner_id, and optionally passwordHash already set.
     * @throws SQLException if the INSERT fails.
     */
    public void createServer(Server server) throws SQLException {
        String sql = "INSERT INTO servers (name, owner_id, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, server.getName());
            stmt.setInt(2, server.getOwnerId());
            // Store NULL when no password was set; otherwise store the BCrypt hash
            if (server.getPasswordHash() != null && !server.getPasswordHash().isEmpty()) {
                stmt.setString(3, server.getPasswordHash());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            stmt.executeUpdate();

            // Write the generated ID back to the model so the caller has it
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    server.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Returns all servers that the given user is a member of.
     * Joins the servers table with user_server_map to filter by userId.
     * Used to populate the server sidebar for the logged-in user.
     *
     * @param userId The user whose server memberships we want to retrieve.
     * @return A List of Server objects the user belongs to (may be empty).
     * @throws SQLException if the JOIN query fails.
     */
    public List<Server> getServersByUser(int userId) throws SQLException {
        String sql = "SELECT s.* FROM servers s " +
                     "JOIN user_server_map usm ON s.id = usm.server_id " +
                     "WHERE usm.user_id = ?";
        List<Server> servers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    servers.add(mapRow(rs));
                }
            }
        }
        return servers;
    }

    /**
     * Returns every server in the system, regardless of membership.
     * Used by the server discovery / "Browse Servers" page so users can
     * find and join servers they're not yet a member of.
     *
     * @return A List of all Server objects ordered by creation date (newest first).
     * @throws SQLException if the query fails.
     */
    public List<Server> getAllServers() throws SQLException {
        String sql = "SELECT * FROM servers ORDER BY created_at DESC";
        List<Server> servers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                servers.add(mapRow(rs));
            }
        }
        return servers;
    }

    /**
     * Returns a single Server by its primary key, or null if not found.
     * Used by JoinServerServlet to load the server before password validation.
     *
     * @param serverId The primary key of the server to fetch.
     * @return The matching Server, or null if no such row exists.
     * @throws SQLException if the query fails.
     */
    public Server getServerById(int serverId) throws SQLException {
        String sql = "SELECT * FROM servers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the given plain-text password matches the BCrypt hash
     * stored for the specified server.
     * Returns true if the server has no password (open server).
     *
     * @param serverId      The server to check.
     * @param plainPassword The password typed by the user trying to join.
     * @return true if the password is correct (or the server is open), false otherwise.
     * @throws SQLException if fetching the server record fails.
     */
    public boolean verifyServerPassword(int serverId, String plainPassword) throws SQLException {
        Server server = getServerById(serverId);
        if (server == null) return false;
        // Open server — no password required
        if (!server.isPasswordProtected()) return true;
        // Password-protected — delegate BCrypt check to PasswordUtil
        return PasswordUtil.checkPassword(plainPassword, server.getPasswordHash());
    }

    // ── Private helper ────────────────────────────────────────────────────────

    /**
     * Maps a single ResultSet row to a Server model object.
     * Centralised here so every query stays DRY.
     */
    private Server mapRow(ResultSet rs) throws SQLException {
        Server server = new Server();
        server.setId(rs.getInt("id"));
        server.setName(rs.getString("name"));
        server.setOwnerId(rs.getInt("owner_id"));
        server.setCreatedAt(rs.getTimestamp("created_at"));
        server.setPasswordHash(rs.getString("password_hash")); // may be null for open servers
        return server;
    }
}
