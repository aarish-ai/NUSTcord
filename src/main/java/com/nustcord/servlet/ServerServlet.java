package com.nustcord.servlet;

/**
 * ServerServlet.java
 * Purpose: Handles server creation actions submitted from the UI.
 * Key Responsibilities:
 *  - Validate the user's session before allowing server creation
 *  - Delegate to ServerService to create the server, default channel, and admin role
 *  - Redirect the user to serverList.jsp after successful creation
 *  - Handle SQL errors gracefully
 * Created: 2026-05-12
 */

import com.nustcord.dao.ServerDAO;
import com.nustcord.dao.UserServerMapDAO;
import com.nustcord.model.Server;
import com.nustcord.service.ServerService;
import com.nustcord.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller for server management operations.
 * Currently supports the "create" action; future actions (delete, rename) can be added here.
 * Maps to /server (used by serverList.jsp create-server form).
 */
@WebServlet("/server")
public class ServerServlet extends HttpServlet {

    // ServerService orchestrates server creation (server row + admin role + default channel)
    private ServerService serverService;

    /**
     * Called once when the servlet is first loaded.
     * Initialises ServerService with its required DAO dependencies.
     */
    @Override
    public void init() {
        serverService = new ServerService(new ServerDAO(), new UserServerMapDAO());
    }

    /**
     * Handles all POST requests for server operations.
     * The "action" parameter determines what to do (currently only "create" is supported).
     *
     * For "create":
     *  1. Verify the user is logged in (session check)
     *  2. Read the server name from the POST body
     *  3. Call serverService.createServer() which handles the full creation workflow
     *  4. Redirect to serverList.jsp to show the new server
     *
     * @param req  must contain "action" = "create" and "name" = server name
     * @param resp used to redirect after creation
     * @throws IOException      if the redirect fails
     * @throws ServletException wrapping any SQLException from the service layer
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // Read the action parameter to determine which server operation to perform
        String action = req.getParameter("action");

        // Guard: ensure the user is authenticated before creating a server
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // Not logged in – redirect to login page
            resp.sendRedirect("login.jsp");
            return;
        }

        // Retrieve the owner's ID from the session (used to assign admin role)
        int userId = (int) session.getAttribute("userId");

        try {
            if ("create".equals(action)) {
                // Read the desired server name from the POST body
                String name = req.getParameter("name");

                // Optional server password — empty string means "no password" (open server)
                String password = req.getParameter("password");

                // Build a Server model and let ServerService fill in the generated ID
                Server server = new Server();
                server.setName(name);

                // BCrypt-hash the password only if one was actually provided
                if (password != null && !password.trim().isEmpty()) {
                    server.setPasswordHash(PasswordUtil.hashPassword(password.trim()));
                }
                // If no password, passwordHash stays null → open server

                // ServerService creates: 1) server row, 2) Admin role, 3) owner membership, 4) #general channel
                serverService.createServer(server, userId);

                // Redirect to the server list page so the new server is immediately visible
                resp.sendRedirect("serverList.jsp?success=Server+created+successfully");
            }
        } catch (SQLException e) {
            // Wrap SQL errors as servlet exceptions; the container will show the error page
            throw new ServletException(e);
        }
    }
}
