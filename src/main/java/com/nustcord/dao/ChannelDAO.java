package com.nustcord.dao;

import com.nustcord.model.Channel;
import com.nustcord.model.ChannelType;
import com.nustcord.util.DBConnection;
import java.sql.*;
import java.util.*;

public class ChannelDAO {

    public void createChannel(Channel channel) throws SQLException {
        String sql = "INSERT INTO channels (server_id, name, type) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, channel.getServerId());
            stmt.setString(2, channel.getName());
            stmt.setString(3, channel.getType().name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    channel.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Channel> getChannelsByServer(int serverId) throws SQLException {
        String sql = "SELECT * FROM channels WHERE server_id = ?";
        List<Channel> channels = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Channel channel = new Channel();
                    channel.setId(rs.getInt("id"));
                    channel.setServerId(rs.getInt("server_id"));
                    channel.setName(rs.getString("name"));
                    channel.setType(ChannelType.valueOf(rs.getString("type")));
                    channel.setCreatedAt(rs.getTimestamp("created_at"));
                    channels.add(channel);
                }
            }
        }
        return channels;
    }
}
