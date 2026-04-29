package com.nustcord.dao;

import com.nustcord.model.Server;
import java.sql.*;
import java.util.*;

public class ServerDAO {
    private Connection conn;

    public ServerDAO(Connection conn) {
        this.conn = conn;
    }

    public void createServer(Server server) throws SQLException {
        String sql = "INSERT INTO servers (name, owner_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, server.getName());
            stmt.setInt(2, server.getOwnerId());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                server.setId(rs.getInt(1));
            }
        }
    }

    public List<Server> getServersByUser(int userId) throws SQLException {
        String sql = "SELECT s.* FROM servers s " +
                     "JOIN user_server_map usm ON s.id = usm.server_id " +
                     "WHERE usm.user_id = ?";
        List<Server> servers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
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
