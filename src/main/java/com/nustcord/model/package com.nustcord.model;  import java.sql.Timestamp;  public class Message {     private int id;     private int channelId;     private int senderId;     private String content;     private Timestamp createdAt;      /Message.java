package com.nustcord.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int channelId;
    private int senderId;
    private String content;
    private Timestamp createdAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getChannelId() { return channelId; }
    public void setChannelId(int channelId) { this.channelId = channelId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
