package com.nustcord.service;

import com.nustcord.dao.ChannelDAO;
import com.nustcord.model.Channel;
import java.sql.SQLException;
import java.util.List;

public class ChannelService {
    private ChannelDAO channelDAO;

    public ChannelService(ChannelDAO channelDAO) {
        this.channelDAO = channelDAO;
    }

    public void createChannel(Channel channel) throws SQLException {
        channelDAO.createChannel(channel);
    }

    public List<Channel> getChannels(int serverId) throws SQLException {
        return channelDAO.getChannelsByServer(serverId);
    }
}
