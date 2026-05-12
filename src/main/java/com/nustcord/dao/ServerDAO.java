package com.nustcord.dao;

/**
 * ServerDAO.java
 * Purpose: Data Access Object for server-related database operations.
 * Key Responsibilities:
 *  - Create new servers (INSERT with generated-key retrieval)
 *  - List servers a specific user has joined (JOIN query)
 *  - List all servers in the system (for server discovery page)
 * Created: 2026-05-12
 */

import com.nustcord.model.Server;
import com.nustcord.util.DBConnection;
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
     *
     * @param server A Server object with name and owner_id already set.
     * @throws SQLException if the INSERT fails.
     */
    public void createServer(Server server) throws SQLException {
        // RETURN_GENERATED_KEYS allows us to retrieve the new server's ID
        String sql = "INSERT INTO servers (name, owner_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, server.getName());
            stmt.setInt(2, server.getOwnerId());
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
        // JOIN with user_server_map to find servers this user has joined
        String sql = "SELECT s.* FROM servers s " +
                     "JOIN user_server_map usm ON s.id = usm.server_id " +
                     "WHERE usm.user_id = ?";
        List<Server> servers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                // Map each row to a Server model object
                while (rs.next()) {
                    Server server = new Server();
                    server.setId(rs.getInt("id"));
                    server.setName(rs.getString("name"));
                    server.setOwnerId(rs.getInt("owner_id"));
                    server.setCreatedAt(rs.getTimestamp("created_at"));
                    servers.add(server);
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
        // Simple full-table scan, ordered newest-first for a better UX in the browse list
        String sql = "SELECT * FROM servers ORDER BY created_at DESC";
        List<Server> servers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Server server = new Server();
                server.setId(rs.getInt("id"));
                server.setName(rs.getString("name"));
                server.setOwnerId(rs.getInt("owner_id"));
                server.setCreatedAt(rs.getTimestamp("created_at"));
                servers.add(server);
            }
        }
        return servers;
    }
}
