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
        int serverId = Integer.parseInt(req.getParameter("serverId"));
        String name = req.getParameter("name");
        String type = req.getParameter("type");

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
