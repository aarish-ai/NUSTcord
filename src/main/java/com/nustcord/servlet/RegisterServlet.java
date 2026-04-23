package com.nustcord.servlet;

import com.nustcord.exception.AuthException;
import com.nustcord.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String user = request.getParameter("username");
        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        try {
            if (authService.register(user, email, pass)) {
                response.sendRedirect("login.jsp?success=Registration successful. Please log in.");
            } else {
                response.sendRedirect("register.jsp?error=Registration failed.");
            }
        } catch (AuthException e) {
            response.sendRedirect("register.jsp?error=" + e.getMessage());
        }
    }
}
