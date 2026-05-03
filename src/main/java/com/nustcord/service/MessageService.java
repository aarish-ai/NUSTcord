package com.nustcord.service;

import com.nustcord.dao.MessageDAO;
import com.nustcord.model.Message;
import java.sql.SQLException;
import java.util.List;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    // Send a new message
    public void sendMessage(Message message) throws SQLException {
        messageDAO.saveMessage(message);
    }

    // Get all messages for a channel
    public List<Message> getMessages(int channelId) throws SQLException {
        return messageDAO.getMessagesByChannel(channelId);
    }
}
