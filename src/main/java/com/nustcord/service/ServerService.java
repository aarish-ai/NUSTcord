package com.nustcord.service;

import com.nustcord.dao.ChannelDAO;
import com.nustcord.dao.RoleDAO;
import com.nustcord.dao.ServerDAO;
import com.nustcord.dao.UserServerMapDAO;
import com.nustcord.model.Channel;
import com.nustcord.model.ChannelType;
import com.nustcord.model.Role;
import com.nustcord.model.Server;
import java.sql.SQLException;
import java.util.List;

public class ServerService {
    private ServerDAO serverDAO;
    private UserServerMapDAO userServerMapDAO;
    private RoleDAO roleDAO;
    private ChannelDAO channelDAO;

    public ServerService() {
        this.serverDAO = new ServerDAO();
        this.userServerMapDAO = new UserServerMapDAO();
        this.roleDAO = new RoleDAO();
        this.channelDAO = new ChannelDAO();
    }

    public ServerService(ServerDAO serverDAO, UserServerMapDAO userServerMapDAO) {
        this.serverDAO = serverDAO;
        this.userServerMapDAO = userServerMapDAO;
        this.roleDAO = new RoleDAO();
        this.channelDAO = new ChannelDAO();
    }

    public void createServer(Server server, int ownerId) throws SQLException {
        server.setOwnerId(ownerId);
        serverDAO.createServer(server);

        // 1. Create an Admin Role for the server
        Role adminRole = new Role();
        adminRole.setServerId(server.getId());
        adminRole.setName("Admin");
        adminRole.setPermissions("ALL");
        roleDAO.createRole(adminRole);

        // 2. Map the owner to the new server with the admin role
        userServerMapDAO.joinServer(ownerId, server.getId(), adminRole.getId());

        // 3. Create a default "general" text channel
        Channel generalChannel = new Channel();
        generalChannel.setServerId(server.getId());
        generalChannel.setName("general");
        generalChannel.setType(ChannelType.TEXT);
        channelDAO.createChannel(generalChannel);
    }

    public List<Server> getUserServers(int userId) throws SQLException {
        return serverDAO.getServersByUser(userId);
    }
}
