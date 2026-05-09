package com.nustcord.dao;

import com.nustcord.util.DBConnection;
import java.sql.*;

public class UserServerMapDAO {

    public void joinServer(int userId, int serverId, Integer roleId) throws SQLException {
        String sql = "INSERT INTO user_server_map (user_id, server_id, role_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, serverId);
            if (roleId != null) {
                stmt.setInt(3, roleId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.executeUpdate();
        }
    }

    public boolean isUserInServer(int userId, int serverId) throws SQLException {
        String sql = "SELECT count(*) FROM user_server_map WHERE user_id = ? AND server_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, serverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
