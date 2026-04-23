package com.nustcord.model;

import java.sql.Timestamp;

public class UserStatus {
    private int userId;
    private String status; // 'Online', 'Offline', 'Busy', 'Away'
    private Timestamp lastUpdated;

    public UserStatus() {}

    public UserStatus(int userId, String status, Timestamp lastUpdated) {
        this.userId = userId;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }
}
