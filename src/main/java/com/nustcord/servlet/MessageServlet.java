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
        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
        messageService = new MessageService(new MessageDAO(conn));
    }

    // Handle sending a new message
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        int senderId = (int) session.getAttribute("userId");
        int channelId = Integer.parseInt(req.getParameter("channelId"));
        String content = req.getParameter("content");

        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setChannelId(channelId);
        msg.setContent(content);

        try {
            messageService.sendMessage(msg);
            resp.sendRedirect("chat.jsp?channelId=" + channelId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    // Handle retrieving messages for a channel
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        int channelId = Integer.parseInt(req.getParameter("channelId"));
        try {
            List<Message> messages = messageService.getMessages(channelId);
            req.setAttribute("messages", messages);
            req.getRequestDispatcher("chat.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
