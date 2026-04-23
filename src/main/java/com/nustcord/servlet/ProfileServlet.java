package com.nustcord.servlet;

import com.nustcord.service.ProfileService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String displayName = request.getParameter("displayName");
        String bio = request.getParameter("bio");

        if (profileService.updateProfile(userId, displayName, bio)) {
            response.sendRedirect("profile.jsp?success=Profile updated successfully.");
        } else {
            response.sendRedirect("profile.jsp?error=Failed to update profile.");
        }
    }
}
