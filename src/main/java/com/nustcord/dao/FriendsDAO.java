package com.nustcord.dao;

/**
 * FriendsDAO.java
 * Purpose: Data Access Object for accepted friend relationships.
 * Key Responsibilities:
 *  - Insert a new friendship pair into the "friends" table when a request is accepted
 *  - Retrieve all friendships for a given user from either direction
 *  - Enforce consistent ID ordering (smaller ID in user_id1) to prevent duplicates
 * Created: 2026-05-12
 */

import com.nustcord.model.Friend;
import com.nustcord.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the "friends" table which stores finalized, accepted friendships.
 * Works alongside FriendRequestDAO: requests are accepted there, then
 * the accepted pair is added here.
 *
 * ID ordering convention: the smaller user_id is always stored in user_id1.
 * This prevents logical duplicates like (1, 2) and (2, 1) occupying two rows.
 */
public class FriendsDAO {

    /**
     * Inserts a new accepted friendship between two users.
     * Enforces consistent ordering by always placing the smaller ID in user_id1.
     * Uses INSERT IGNORE so accidental duplicate calls don't throw exceptions.
     *
     * @param userId1 One side of the friendship (order doesn't matter – we sort internally).
     * @param userId2 The other side of the friendship.
     * @return true if the row was inserted, false if it already existed or an error occurred.
     */
    public boolean addFriend(int userId1, int userId2) {
        // Enforce a consistent order (smaller ID first) to avoid duplicates like (1,2) and (2,1)
        int id1 = Math.min(userId1, userId2);
        int id2 = Math.max(userId1, userId2);

        // INSERT IGNORE silently skips insertion if the PRIMARY KEY pair already exists
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

    /**
     * Returns all friends for a given user.
     * Since a user can appear in either the user_id1 or user_id2 column, we
     * use an OR condition to find all rows where they appear.
     *
     * @param userId The user whose friends we want to retrieve.
     * @return A List of Friend objects (may be empty if the user has no friends yet).
     */
    public List<Friend> getFriendsForUser(int userId) {
        List<Friend> friends = new ArrayList<>();
        // Query both columns because our ID-ordering means the user could be in either position
        String sql = "SELECT user_id1, user_id2, created_at FROM friends WHERE user_id1 = ? OR user_id2 = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind the userId for both the OR conditions
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Map the row to a Friend model object
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
