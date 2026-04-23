package com.nustcord.model;

import java.sql.Timestamp;

public class FriendRequest {
    private int id;
    private int senderId;
    private int receiverId;
    private String status; // 'PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED'
    private Timestamp createdAt;

    // Transient attributes for UI convenience
    private String senderUsername;
    private String receiverUsername;

    public FriendRequest() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }
}
