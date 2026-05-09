package com.nustcord.dao;

import com.nustcord.model.Server;
import com.nustcord.util.DBConnection;
import java.sql.*;
import java.util.*;

public class ServerDAO {

    public void createServer(Server server) throws SQLException {
        String sql = "INSERT INTO servers (name, owner_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, server.getName());
            stmt.setInt(2, server.getOwnerId());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    server.setId(rs.getInt(1));
                }
            }
        }
    }

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

    public List<Server> getAllServers() throws SQLException {
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
