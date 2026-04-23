package com.nustcord.service;

import com.nustcord.dao.ProfileDAO;
import com.nustcord.dao.StatusDAO;
import com.nustcord.model.Profile;
import com.nustcord.model.UserStatus;

public class ProfileService {

    private final ProfileDAO profileDAO = new ProfileDAO();
    private final StatusDAO statusDAO = new StatusDAO();

    public Profile getProfile(int userId) {
        Profile p = profileDAO.getProfileByUserId(userId);
        if (p == null) {
            p = new Profile(userId, "New User", "I am new here.");
        }
        return p;
    }

    public boolean updateProfile(int userId, String displayName, String bio) {
        Profile profile = new Profile(userId, displayName, bio);
        return profileDAO.createOrUpdateProfile(profile);
    }

    public boolean updateStatus(int userId, String status) {
        if (!status.equals("Online") && !status.equals("Offline") && 
            !status.equals("Busy") && !status.equals("Away")) {
            return false;
        }
        return statusDAO.updateStatus(userId, status);
    }
    
    public UserStatus getStatus(int userId) {
        return statusDAO.getStatusByUserId(userId);
    }
}
