package com.nustcord.servlet;

import com.nustcord.exception.AuthException;
import com.nustcord.model.User;
import com.nustcord.service.AuthService;
import com.nustcord.service.ProfileService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();
    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        try {
            User loggedInUser = authService.login(user, pass);
            HttpSession session = request.getSession();
            session.setAttribute("userId", loggedInUser.getId());
            session.setAttribute("username", loggedInUser.getUsername());
            
            // Set User Status to Online on login
            profileService.updateStatus(loggedInUser.getId(), "Online");
            
            response.sendRedirect("dashboard.jsp");
        } catch (AuthException e) {
            response.sendRedirect("login.jsp?error=" + e.getMessage());
        }
    }
}
