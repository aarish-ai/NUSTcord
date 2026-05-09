package com.nustcord.servlet;

import com.nustcord.dao.DirectMessageDAO;
import com.nustcord.model.DirectMessage;
import com.nustcord.service.DirectMessageService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/directMessage")
public class DirectMessageServlet extends HttpServlet {
    private DirectMessageService dmService;
    
    @Override
    public void init() {
        dmService = new DirectMessageService(new DirectMessageDAO());
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        String friendIdParam = req.getParameter("friendId");
        
        if (friendIdParam == null || friendIdParam.isEmpty()) {
            resp.sendRedirect("friends.jsp");
            return;
        }
        
        try {
            int friendId = Integer.parseInt(friendIdParam);
            List<DirectMessage> messages = dmService.getMessages(userId, friendId);
            req.setAttribute("messages", messages);
            req.getRequestDispatcher("directMessage.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect("friends.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        String friendIdParam = req.getParameter("friendId");
        String content = req.getParameter("content");
        
        if (friendIdParam == null || friendIdParam.isEmpty() || content == null || content.trim().isEmpty()) {
            resp.sendRedirect("friends.jsp");
            return;
        }
        
        try {
            int friendId = Integer.parseInt(friendIdParam);
            dmService.sendMessage(userId, friendId, content);
            resp.sendRedirect("directMessage?friendId=" + friendId);
        } catch (NumberFormatException e) {
            resp.sendRedirect("friends.jsp");
        }
    }
}
