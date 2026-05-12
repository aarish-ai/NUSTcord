package com.nustcord.servlet;

/**
 * MessageServlet.java
 * Purpose: Handles sending and retrieving chat messages in server channels.
 * Key Responsibilities:
 *  - doPost: validate session + params, save a new message via MessageService,
 *            then redirect back to the chat view
 *  - doGet:  validate params, load messages for a channel via MessageService,
 *            set them as a request attribute, then forward to chat.jsp
 *  - All inputs are validated defensively to avoid HTTP 500 errors
 * Created: 2026-05-12
 */

import com.nustcord.dao.MessageDAO;
import com.nustcord.model.Message;
import com.nustcord.service.MessageService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Serves as the controller for all server channel messaging actions.
 * Maps to the URL pattern /message (used by chat.jsp forms and redirects).
 */
@WebServlet("/message")
public class MessageServlet extends HttpServlet {

    // MessageService encapsulates the business logic (validation, persistence)
    private MessageService messageService;

    /**
     * Called once by the servlet container on first load.
     * Initialises MessageService with a concrete MessageDAO implementation.
     */
    @Override
    public void init() {
        messageService = new MessageService(new MessageDAO());
    }

    /**
     * Handles the HTTP POST request for sending a new message.
     * Steps:
     *  1. Verify the user is logged in (session check)
     *  2. Validate channelId parameter is present and numeric
     *  3. Validate the message content is not blank
     *  4. Build a Message model and call messageService.sendMessage()
     *  5. Redirect back to the channel's GET URL to display updated messages
     *
     * @param req  the HTTP request (must contain channelId, content, optionally serverId)
     * @param resp used to redirect after successful send
     * @throws IOException      if the redirect fails
     * @throws ServletException if the underlying service throws unexpectedly
     */
    // Handle sending a new message
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        // Check if user session exists and is not expired
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // No valid session – redirect to login page
            resp.sendRedirect("login.jsp");
            return;
        }

        // Retrieve userId from session (stored as Integer by LoginServlet)
        int senderId = (int) session.getAttribute("userId");

        // Safely parse channelId parameter with null check
        String channelIdParam = req.getParameter("channelId");
        if (channelIdParam == null || channelIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing channelId");
            return;
        }

        int channelId;
        try {
            channelId = Integer.parseInt(channelIdParam);
        } catch (NumberFormatException e) {
            // channelId was not a valid integer – could be a malformed URL
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid channelId format");
            return;
        }

        // Validate message content – don't store blank messages
        String content = req.getParameter("content");
        if (content == null || content.trim().isEmpty()) {
            resp.sendRedirect("chat.jsp?channelId=" + channelId + "&error=empty_message");
            return;
        }

        // serverId is optional but needed for the redirect URL so the chat sidebar renders correctly
        String serverIdParam = req.getParameter("serverId");

        // Build the message object to pass to the service layer
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setChannelId(channelId);
        msg.setContent(content);

        try {
            // Persist the message to the database
            messageService.sendMessage(msg);

            // Build the redirect URL back to this channel's chat view
            String redirectUrl = "message?channelId=" + channelId;
            if (serverIdParam != null && !serverIdParam.isEmpty()) {
                redirectUrl += "&serverId=" + serverIdParam;
            }
            resp.sendRedirect(redirectUrl);

        } catch (SQLException e) {
            // Database error – wrap in ServletException; container will show error page
            throw new ServletException(e);
        }
    }

    /**
     * Handles the HTTP GET request for viewing messages in a channel.
     * Steps:
     *  1. Validate channelId parameter
     *  2. Load messages from the database via MessageService
     *  3. Store the message list as a request attribute
     *  4. Forward to chat.jsp which renders the HTML chat view
     *
     * @param req  must contain a valid "channelId" parameter
     * @param resp used to forward to chat.jsp
     * @throws IOException      if the forward fails
     * @throws ServletException if the database or forward throws
     */
    // Handle retrieving messages for a channel
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        // Validate channelId before attempting any database query
        String channelIdParam = req.getParameter("channelId");
        if (channelIdParam == null || channelIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing channelId");
            return;
        }

        int channelId;
        try {
            channelId = Integer.parseInt(channelIdParam);
        } catch (NumberFormatException e) {
            // Invalid integer in URL – reject with 400 rather than crash
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid channelId format");
            return;
        }

        try {
            // Fetch all messages for this channel, ordered oldest-first
            List<Message> messages = messageService.getMessages(channelId);

            // Set messages as a request attribute so chat.jsp can access them
            req.setAttribute("messages", messages);

            // Also pass serverId through if present (needed by chat.jsp sidebar)
            String serverIdParam = req.getParameter("serverId");
            if (serverIdParam != null && !serverIdParam.isEmpty()) {
                req.setAttribute("serverId", serverIdParam);
            }

            // Forward to the view – req attributes travel with the forward
            req.getRequestDispatcher("chat.jsp").forward(req, resp);

        } catch (SQLException e) {
            // Propagate database exceptions as servlet exceptions for container handling
            throw new ServletException(e);
        }
    }
}
