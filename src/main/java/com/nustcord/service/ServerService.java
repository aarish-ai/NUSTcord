package com.nustcord.service;

import com.nustcord.dao.ServerDAO;
import com.nustcord.dao.UserServerMapDAO;
import com.nustcord.model.Server;
import java.sql.SQLException;
import java.util.List;

public class ServerService {
    private ServerDAO serverDAO;
    private UserServerMapDAO userServerMapDAO;

    public ServerService(ServerDAO serverDAO, UserServerMapDAO userServerMapDAO) {
        this.serverDAO = serverDAO;
        this.userServerMapDAO = userServerMapDAO;
    }

    public void createServer(Server server, int ownerId) throws SQLException {
        server.setOwnerId(ownerId);
        serverDAO.createServer(server);
        // Owner automatically joins with admin role (roleId = 1 for now)
        userServerMapDAO.joinServer(ownerId, server.getId(), 1);
    }

    public List<Server> getUserServers(int userId) throws SQLException {
        return serverDAO.getServersByUser(userId);
    }
}
