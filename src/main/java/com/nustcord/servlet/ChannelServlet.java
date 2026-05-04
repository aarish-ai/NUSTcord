package com.nustcord.servlet;

import com.nustcord.dao.ChannelDAO;
import com.nustcord.model.Channel;
import com.nustcord.model.ChannelType;
import com.nustcord.service.ChannelService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/channel")
public class ChannelServlet extends HttpServlet {
    private ChannelService channelService;

    @Override
    public void init() {
        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
        channelService = new ChannelService(new ChannelDAO(conn));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String serverIdParam = req.getParameter("serverId");
        if (serverIdParam == null || serverIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing serverId");
            return;
        }
        int serverId;
        try {
            serverId = Integer.parseInt(serverIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid serverId format");
            return;
        }

        String name = req.getParameter("name");
        String type = req.getParameter("type");
        
        if (name == null || type == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing channel parameters");
            return;
        }

        Channel channel = new Channel();
        channel.setServerId(serverId);
        channel.setName(name);
        channel.setType(ChannelType.valueOf(type));

        try {
            channelService.createChannel(channel);
            resp.sendRedirect("channelView.jsp?serverId=" + serverId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
