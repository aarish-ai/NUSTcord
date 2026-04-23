package com.nustcord.servlet;

import com.nustcord.service.FriendService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/FriendServlet")
public class FriendServlet extends HttpServlet {
    private final FriendService friendService = new FriendService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp"); return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");
        
        try {
            if ("send".equals(action)) {
                int receiverId = Integer.parseInt(request.getParameter("receiverId"));
                friendService.sendFriendRequest(userId, receiverId);
                response.sendRedirect("friends.jsp?success=Request sent.");
            } else if ("accept".equals(action)) {
                int requestId = Integer.parseInt(request.getParameter("requestId"));
                friendService.acceptRequest(requestId, userId);
                response.sendRedirect("friends.jsp?success=Request accepted.");
            } else if ("reject".equals(action)) {
                int requestId = Integer.parseInt(request.getParameter("requestId"));
                friendService.rejectRequest(requestId, userId);
                response.sendRedirect("friends.jsp?success=Request rejected.");
            }
        } catch (Exception e) {
            response.sendRedirect("friends.jsp?error=" + e.getMessage());
        }
    }
}
