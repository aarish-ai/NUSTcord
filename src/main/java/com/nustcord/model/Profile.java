package com.nustcord.model;

public class Profile {
    private int userId;
    private String displayName;
    private String bio;

    public Profile() {}

    public Profile(int userId, String displayName, String bio) {
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
