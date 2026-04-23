package com.nustcord.dao;

import com.nustcord.model.Friend;
import com.nustcord.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsDAO {

    public boolean addFriend(int userId1, int userId2) {
        // Enforce a consistent order (smaller ID first) to avoid duplicates like (1,2) and (2,1)
        int id1 = Math.min(userId1, userId2);
        int id2 = Math.max(userId1, userId2);

        String sql = "INSERT IGNORE INTO friends (user_id1, user_id2) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id1);
            stmt.setInt(2, id2);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Friend> getFriendsForUser(int userId) {
        List<Friend> friends = new ArrayList<>();
        String sql = "SELECT user_id1, user_id2, created_at FROM friends WHERE user_id1 = ? OR user_id2 = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Friend f = new Friend();
                    f.setUserId1(rs.getInt("user_id1"));
                    f.setUserId2(rs.getInt("user_id2"));
                    f.setCreatedAt(rs.getTimestamp("created_at"));
                    friends.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
