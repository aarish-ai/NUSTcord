package com.nustcord.model;

import java.sql.Timestamp;

public class Friend {
    private int userId1;
    private int userId2;
    private Timestamp createdAt;
    
    // Transient attribute for UI
    private String friendUsername;

    public Friend() {}

    public int getUserId1() { return userId1; }
    public void setUserId1(int userId1) { this.userId1 = userId1; }

    public int getUserId2() { return userId2; }
    public void setUserId2(int userId2) { this.userId2 = userId2; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getFriendUsername() { return friendUsername; }
    public void setFriendUsername(String friendUsername) { this.friendUsername = friendUsername; }
}
