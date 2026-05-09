package com.nustcord.service;

import com.nustcord.dao.DirectMessageDAO;
import com.nustcord.model.DirectMessage;
import java.util.List;

public class DirectMessageService {
    private DirectMessageDAO dmDAO;
    public DirectMessageService(DirectMessageDAO dmDAO) { this.dmDAO = dmDAO; }
    
    public List<DirectMessage> getMessages(int userId1, int userId2) {
        return dmDAO.getMessages(userId1, userId2);
    }
    
    public boolean sendMessage(int senderId, int receiverId, String content) {
        if(content == null || content.trim().isEmpty()) return false;
        DirectMessage msg = new DirectMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content.trim());
        return dmDAO.saveMessage(msg);
    }
}
