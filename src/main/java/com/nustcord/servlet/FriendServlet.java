package com.nustcord.servlet;

/**
 * FriendServlet.java
 * Purpose: Handles friend request actions (send, accept, reject).
 * Key Responsibilities:
 *  - Validate that a user session exists before processing any action
 *  - Dispatch to FriendService based on the "action" request parameter
 *  - Look up the target user by username for the "send" action
 *  - Redirect back to friends.jsp with success or error messages
 * Created: 2026-05-12
 */

import com.nustcord.service.FriendService;
import com.nustcord.dao.UserDAO;
import com.nustcord.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller for all friend-related POST operations.
 * Maps to /FriendServlet (used by friends.jsp forms).
 * All three actions (send, accept, reject) are routed through this single servlet
 * using the "action" parameter to differentiate between them.
 */
@WebServlet("/FriendServlet")
public class FriendServlet extends HttpServlet {

    // FriendService contains the business logic for the two-step friend request flow
    private final FriendService friendService = new FriendService();

    /**
     * Processes all friend request actions submitted from friends.jsp.
     *
     * The "action" parameter determines what operation to perform:
     *  - "send":   creates a new pending friend request from this user to a target username
     *  - "accept": marks the request as accepted and adds both users to the friends table
     *  - "reject": marks the request as rejected
     *
     * On any error (user not found, invalid IDs), the user is redirected to
     * friends.jsp with an "error" query parameter containing the message.
     *
     * @param request  must contain "action" and relevant secondary params
     * @param response used to redirect the user after the action
     * @throws ServletException propagated from underlying layers
     * @throws IOException      if the redirect fails
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Verify the user is logged in before allowing any friend operations
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // Not logged in – redirect to login page
            response.sendRedirect("login.jsp"); return;
        }

        // Retrieve the logged-in user's ID from the session
        int userId = (Integer) session.getAttribute("userId");

        // Read the action parameter to determine which operation to perform
        String action = request.getParameter("action");

        try {
            if ("send".equals(action)) {
                // "send" action: look up the target user by their username string
                // (users enter a username, not an ID, for a friendlier UX)
                String receiverUsername = request.getParameter("receiverUsername");
                UserDAO userDAO = new UserDAO();

                // Look up the target user – returns null if username doesn't exist
                User receiver = userDAO.getUserByUsername(receiverUsername);
                if (receiver == null) {
                    throw new Exception("User '" + receiverUsername + "' not found.");
                }

                // Delegate to FriendService which creates the request in the DB
                friendService.sendFriendRequest(userId, receiver.getId());
                response.sendRedirect("friends.jsp?success=Request sent.");

            } else if ("accept".equals(action)) {
                // "accept" action: update the request status and create a friends row
                int requestId = Integer.parseInt(request.getParameter("requestId"));
                friendService.acceptRequest(requestId, userId);
                response.sendRedirect("friends.jsp?success=Request accepted.");

            } else if ("reject".equals(action)) {
                // "reject" action: mark the request as REJECTED (no friends row created)
                int requestId = Integer.parseInt(request.getParameter("requestId"));
                friendService.rejectRequest(requestId, userId);
                response.sendRedirect("friends.jsp?success=Request rejected.");
            }

        } catch (Exception e) {
            // Catch-all: any service-level exception is shown as an error message on friends.jsp
            response.sendRedirect("friends.jsp?error=" + e.getMessage());
        }
    }
}
