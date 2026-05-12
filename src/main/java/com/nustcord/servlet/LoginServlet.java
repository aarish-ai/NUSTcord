package com.nustcord.servlet;

/**
 * LoginServlet.java
 * Purpose: Handles user authentication for the NUSTcord application.
 * Key Responsibilities:
 *  - Process POST requests from login.jsp (username + password form)
 *  - Delegate credential validation to AuthService
 *  - On success: create an HTTP session with userId + username, update status to Online
 *  - On success as admin: redirect directly to the admin dashboard
 *  - On failure: redirect back to login.jsp with a descriptive error message
 * Created: 2026-05-12
 */

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

/**
 * Processes login form submissions.
 * Maps to the URL pattern /LoginServlet (used by login.jsp's form action).
 * Relies on AuthService for credential validation and BCrypt comparison.
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    // Service layer handles all authentication business logic
    private final AuthService authService = new AuthService();

    // ProfileService is needed to update the user's online status upon login
    private final ProfileService profileService = new ProfileService();

    // Hardcoded admin username – must match AdminServlet's constant
    private static final String ADMIN_USERNAME = "admin";

    /**
     * Handles the HTTP POST request submitted by the login form.
     * Steps:
     *   1. Read username and password from the form parameters
     *   2. Call authService.login() which validates against the database
     *   3. If valid: store userId and username in the session, set status Online
     *   4. If the logged-in user is "admin", redirect to /admin dashboard
     *   5. Otherwise redirect to loading.jsp for normal users
     *   6. If AuthException is thrown, redirect to login.jsp with the error
     *
     * @param request  contains form parameters "username" and "password"
     * @param response used to issue redirects
     * @throws ServletException propagated from underlying I/O
     * @throws IOException      if the redirect fails
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Read raw form input values
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        try {
            // Validate credentials against the database via AuthService.
            // Throws AuthException if username not found or password doesn't match.
            User loggedInUser = authService.login(user, pass);

            // Credentials are valid – create (or reuse) an HTTP session
            HttpSession session = request.getSession();

            // Store the user's primary key so other servlets can query by ID
            session.setAttribute("userId", loggedInUser.getId());

            // Store the username for display and admin-role checks
            session.setAttribute("username", loggedInUser.getUsername());

            // Mark the user as Online in the user_status table
            profileService.updateStatus(loggedInUser.getId(), "Online");

            // Check if the authenticated user is the admin account.
            // Admin is redirected directly to the admin dashboard.
            if (ADMIN_USERNAME.equals(loggedInUser.getUsername())) {
                response.sendRedirect("admin");
            } else {
                // Regular users go through the loading screen before the dashboard
                response.sendRedirect("loading.jsp");
            }

        } catch (AuthException e) {
            // Authentication failed (bad credentials or user not found).
            // Redirect back to the login page with the exception's message as a query param.
            response.sendRedirect("login.jsp?error=" + e.getMessage());
        }
    }
}
