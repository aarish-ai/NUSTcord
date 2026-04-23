package com.nustcord.dao;

import com.nustcord.model.Profile;
import com.nustcord.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileDAO {

    public Profile getProfileByUserId(int userId) {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Profile(rs.getInt("user_id"), rs.getString("display_name"), rs.getString("bio"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createOrUpdateProfile(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, display_name, bio) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE display_name = VALUES(display_name), bio = VALUES(bio)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getDisplayName());
            stmt.setString(3, profile.getBio());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
