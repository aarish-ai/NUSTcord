package com.nustcord.dao;

import com.nustcord.util.DBConnection;
import java.sql.*;

public class UserServerMapDAO {

    public void joinServer(int userId, int serverId, int roleId) throws SQLException {
        String sql = "INSERT INTO user_server_map (user_id, server_id, role_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, serverId);
            stmt.setInt(3, roleId);
            stmt.executeUpdate();
        }
    }
}
