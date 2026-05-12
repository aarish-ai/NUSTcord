package com.nustcord.dao;

/**
 * MessageDAO.java
 * Purpose: Data Access Object for chat message persistence.
 * Key Responsibilities:
 *  - Save new messages sent in a text channel (INSERT)
 *  - Retrieve all messages for a given channel in chronological order (SELECT)
 *  - Populate Message model objects from ResultSet data
 * Created: 2026-05-12
 */

import com.nustcord.model.Message;
import com.nustcord.util.DBConnection;
import java.sql.*;
import java.util.*;

/**
 * Handles all database operations for the "messages" table.
 * Messages are associated with a channel (channel_id) and a sender (sender_id).
 */
public class MessageDAO {

    /**
     * Persists a new message to the "messages" table and populates the
     * generated primary key back onto the Message object.
     *
     * @param message A Message object with channelId, senderId, and content set.
     * @throws SQLException if the INSERT fails (e.g., missing FK references).
     */
    // Save a new message
    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (channel_id, sender_id, content) VALUES (?, ?, ?)";

        // RETURN_GENERATED_KEYS tells JDBC to give us the auto-incremented ID
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Bind the three required columns in order
            stmt.setInt(1, message.getChannelId());
            stmt.setInt(2, message.getSenderId());
            stmt.setString(3, message.getContent());
            stmt.executeUpdate();

            // Retrieve the database-generated primary key and set it on the model
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Fetches all messages for a specific channel, ordered oldest-first so
     * the chat view renders messages in the correct chronological order.
     *
     * @param channelId The primary key of the channel to query.
     * @return A List of Message objects (may be empty if no messages exist yet).
     * @throws SQLException if the database query fails.
     */
    // Retrieve all messages for a channel
    public List<Message> getMessagesByChannel(int channelId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE channel_id = ? ORDER BY created_at ASC";
        List<Message> messages = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind the channel filter parameter
            stmt.setInt(1, channelId);

            try (ResultSet rs = stmt.executeQuery()) {
                // Map each row to a Message object and add to the result list
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
