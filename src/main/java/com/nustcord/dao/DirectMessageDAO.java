package com.nustcord.dao;

import com.nustcord.model.DirectMessage;
import com.nustcord.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DirectMessageDAO {
    public List<DirectMessage> getMessages(int userId1, int userId2) {
        List<DirectMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM direct_messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, userId1);
             stmt.setInt(2, userId2);
             stmt.setInt(3, userId2);
             stmt.setInt(4, userId1);
             try (ResultSet rs = stmt.executeQuery()) {
                 while (rs.next()) {
                     DirectMessage msg = new DirectMessage();
                     msg.setId(rs.getInt("id"));
                     msg.setSenderId(rs.getInt("sender_id"));
                     msg.setReceiverId(rs.getInt("receiver_id"));
                     msg.setContent(rs.getString("content"));
                     msg.setCreatedAt(rs.getTimestamp("created_at"));
                     messages.add(msg);
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
    public boolean saveMessage(DirectMessage msg) {
        String sql = "INSERT INTO direct_messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, msg.getSenderId());
             stmt.setInt(2, msg.getReceiverId());
             stmt.setString(3, msg.getContent());
             return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DirectMessage> getRecentConversations(int userId) {
        List<DirectMessage> conversations = new ArrayList<>();
        String sql = "SELECT m1.* FROM direct_messages m1 LEFT JOIN direct_messages m2 " +
                     "ON ((m1.sender_id = m2.sender_id AND m1.receiver_id = m2.receiver_id) OR (m1.sender_id = m2.receiver_id AND m1.receiver_id = m2.sender_id)) " +
                     "AND m1.created_at < m2.created_at " +
                     "WHERE m2.id IS NULL AND (m1.sender_id = ? OR m1.receiver_id = ?) " +
                     "ORDER BY m1.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, userId);
             stmt.setInt(2, userId);
             try (ResultSet rs = stmt.executeQuery()) {
                 while (rs.next()) {
                     DirectMessage msg = new DirectMessage();
                     msg.setId(rs.getInt("id"));
                     msg.setSenderId(rs.getInt("sender_id"));
                     msg.setReceiverId(rs.getInt("receiver_id"));
                     msg.setContent(rs.getString("content"));
                     msg.setCreatedAt(rs.getTimestamp("created_at"));
                     conversations.add(msg);
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversations;
    }
}
