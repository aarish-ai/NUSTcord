package com.nustcord.service;

import com.nustcord.dao.FriendRequestDAO;
import com.nustcord.dao.FriendsDAO;
import com.nustcord.model.FriendRequest;

public class FriendService {

    private final FriendRequestDAO requestDAO = new FriendRequestDAO();
    private final FriendsDAO friendsDAO = new FriendsDAO();

    public boolean sendFriendRequest(int senderId, int receiverId) throws Exception {
        if (senderId == receiverId) {
            throw new Exception("Cannot send friend request to yourself.");
        }
        return requestDAO.createRequest(senderId, receiverId);
    }

    public boolean acceptRequest(int requestId, int currentUserId) throws Exception {
        FriendRequest req = requestDAO.getRequestById(requestId);
        if (req == null || req.getReceiverId() != currentUserId || !"PENDING".equals(req.getStatus())) {
            throw new Exception("Invalid request or unauthorized.");
        }
        
        // Update request status to ACCEPTED
        boolean updated = requestDAO.updateRequestStatus(requestId, "ACCEPTED");
        if (updated) {
            // Add to friends table
            return friendsDAO.addFriend(req.getSenderId(), req.getReceiverId());
        }
        return false;
    }

    public boolean rejectRequest(int requestId, int currentUserId) throws Exception {
        FriendRequest req = requestDAO.getRequestById(requestId);
        if (req == null || req.getReceiverId() != currentUserId) {
             throw new Exception("Invalid request or unauthorized.");
        }
        return requestDAO.updateRequestStatus(requestId, "REJECTED");
    }
}
