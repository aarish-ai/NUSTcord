package com.nustcord.dao;

import com.nustcord.model.FriendRequest;
import com.nustcord.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDAO {

    public boolean createRequest(int senderId, int receiverId) {
        String sql = "INSERT INTO friend_requests (sender_id, receiver_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE friend_requests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public FriendRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM friend_requests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
             if (rs.next()) {
                FriendRequest r = new FriendRequest();
                r.setId(rs.getInt("id"));
                r.setSenderId(rs.getInt("sender_id"));
                r.setReceiverId(rs.getInt("receiver_id"));
                r.setStatus(rs.getString("status"));
                return r;
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FriendRequest> getPendingRequestsByReceiver(int receiverId) {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM friend_requests WHERE receiver_id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FriendRequest r = new FriendRequest();
                r.setId(rs.getInt("id"));
                r.setSenderId(rs.getInt("sender_id"));
                r.setReceiverId(rs.getInt("receiver_id"));
                r.setStatus(rs.getString("status"));
                requests.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<FriendRequest> getRequestsBySender(int senderId) {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM friend_requests WHERE sender_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FriendRequest r = new FriendRequest();
                r.setId(rs.getInt("id"));
                r.setSenderId(rs.getInt("sender_id"));
                r.setReceiverId(rs.getInt("receiver_id"));
                r.setStatus(rs.getString("status"));
                requests.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
}
