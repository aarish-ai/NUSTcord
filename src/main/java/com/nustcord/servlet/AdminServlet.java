package com.nustcord.servlet;

/**
 * AdminServlet.java
 * Purpose: HTTP servlet that handles all requests to the /admin URL.
 * Key Responsibilities:
 *  - Validate that only the hardcoded admin account can access this endpoint
 *  - Redirect unauthenticated or non-admin users back to the login page
 *  - Fetch all user statistics via AdminDAO and forward them to adminDashboard.jsp
 *  - Gracefully handle database errors without returning HTTP 500
 * Created: 2026-05-12
 */

import com.nustcord.dao.AdminDAO;
import com.nustcord.dao.AdminDAO.AdminUserRow;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Handles GET requests for the /admin route.
 * Only the user with username "admin" is allowed access; all others are
 * redirected to login.jsp with an error message.
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    // Hardcoded admin credentials as specified in the requirements.
    // In production these would be stored securely (env vars / secrets manager).
    private static final String ADMIN_USERNAME = "admin";

    /**
     * Handles HTTP GET requests for the admin dashboard.
     * Performs three checks before fetching data:
     *   1. Session must exist (user is logged in)
     *   2. Session must contain a "username" attribute
     *   3. That username must equal "admin"
     *
     * @param request  the incoming HTTP request
     * @param response the outgoing HTTP response
     * @throws ServletException if the JSP forward fails unexpectedly
     * @throws IOException      if a redirect or forward causes an I/O error
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve existing session without creating a new one (false = don't create)
        HttpSession session = request.getSession(false);

        // --- Guard 1: No session at all means the user is not logged in ---
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp?error=Please+log+in+first.");
            return;
        }

        // --- Guard 2: Logged-in user must be "admin" ---
        String loggedInUser = (String) session.getAttribute("username");
        if (!ADMIN_USERNAME.equals(loggedInUser)) {
            // Non-admin user tried to access the admin panel
            response.sendRedirect("login.jsp?error=Unauthorized:+Admin+access+only.");
            return;
        }

        // --- Data Fetch: retrieve all users with aggregated stats ---
        AdminDAO adminDAO = new AdminDAO();
        try {
            List<AdminUserRow> users = adminDAO.getAllUsersWithStats();
            // Pass the user list to the JSP as a request attribute
            request.setAttribute("users", users);
        } catch (SQLException e) {
            // Database error: log it server-side and show a friendly error in the JSP
            // Never propagate as HTTP 500 – set an error message attribute instead
            System.err.println("[AdminServlet] Database error: " + e.getMessage());
            request.setAttribute("dbError", "Could not load user data: " + e.getMessage());
        }

        // Forward to the admin dashboard view regardless of DB success/failure
        // The JSP will handle displaying either the table or the error message
        request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
    }

    /**
     * Delegates POST requests to doGet so the admin dashboard can be
     * reached from a form submission as well (e.g., after a search).
     *
     * @param request  the incoming HTTP request
     * @param response the outgoing HTTP response
     * @throws ServletException forwarded from doGet
     * @throws IOException      forwarded from doGet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Reuse the GET logic for simplicity; dashboard is read-only for now
        doGet(request, response);
    }
}
