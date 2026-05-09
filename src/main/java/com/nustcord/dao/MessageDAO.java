package com.nustcord.dao;

import com.nustcord.model.Message;
import com.nustcord.util.DBConnection;
import java.sql.*;
import java.util.*;

public class MessageDAO {

    // Save a new message
    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (channel_id, sender_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getChannelId());
            stmt.setInt(2, message.getSenderId());
            stmt.setString(3, message.getContent());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
            }
        }
    }

    // Retrieve all messages for a channel
    public List<Message> getMessagesByChannel(int channelId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE channel_id = ? ORDER BY created_at ASC";
        List<Message> messages = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, channelId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));
                    msg.setChannelId(rs.getInt("channel_id"));
                    msg.setSenderId(rs.getInt("sender_id"));
                    msg.setContent(rs.getString("content"));
                    msg.setCreatedAt(rs.getTimestamp("created_at"));
                    messages.add(msg);
                }
            }
        }
        return messages;
    }
}
