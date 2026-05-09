package com.nustcord.servlet;

import com.nustcord.dao.MessageDAO;
import com.nustcord.model.Message;
import com.nustcord.service.MessageService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/message")
public class MessageServlet extends HttpServlet {
    private MessageService messageService;

    @Override
    public void init() {
        messageService = new MessageService(new MessageDAO());
    }

    // Handle sending a new message
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        int senderId = (int) session.getAttribute("userId");

        String channelIdParam = req.getParameter("channelId");
        if (channelIdParam == null || channelIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing channelId");
            return;
        }
        int channelId;
        try {
            channelId = Integer.parseInt(channelIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid channelId format");
            return;
        }

        String content = req.getParameter("content");
        if (content == null || content.trim().isEmpty()) {
            resp.sendRedirect("chat.jsp?channelId=" + channelId + "&error=empty_message");
            return;
        }

        String serverIdParam = req.getParameter("serverId");
        
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setChannelId(channelId);
        msg.setContent(content);

        try {
            messageService.sendMessage(msg);
            String redirectUrl = "message?channelId=" + channelId;
            if (serverIdParam != null && !serverIdParam.isEmpty()) {
                redirectUrl += "&serverId=" + serverIdParam;
            }
            resp.sendRedirect(redirectUrl);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    // Handle retrieving messages for a channel
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String channelIdParam = req.getParameter("channelId");
        if (channelIdParam == null || channelIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing channelId");
            return;
        }
        int channelId;
        try {
            channelId = Integer.parseInt(channelIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid channelId format");
            return;
        }
        try {
            List<Message> messages = messageService.getMessages(channelId);
            req.setAttribute("messages", messages);
            String serverIdParam = req.getParameter("serverId");
            if (serverIdParam != null && !serverIdParam.isEmpty()) {
                req.setAttribute("serverId", serverIdParam);
            }
            req.getRequestDispatcher("chat.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
