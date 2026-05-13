package com.nustcord.servlet;

/**
 * JoinServerServlet.java
 * Purpose: Handles a user's request to join an existing server.
 * Key Responsibilities:
 *  - Authenticate the session before allowing any action
 *  - For password-protected servers, validate the submitted password via BCrypt
 *  - Reject the join with a clear error message on wrong password
 *  - Add the user to the server membership table on success
 * Created: 2026-05-12
 * Updated: 2026-05-13 — added server-password validation
 */

import com.nustcord.dao.ServerDAO;
import com.nustcord.dao.UserServerMapDAO;
import com.nustcord.model.Server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Processes POST requests from the "Join" button on serverList.jsp.
 * If the server has a password, the submitted password is BCrypt-verified
 * before the membership row is inserted.
 */
@WebServlet("/joinServer")
public class JoinServerServlet extends HttpServlet {

    private UserServerMapDAO mapDao;
    private ServerDAO serverDao;

    @Override
    public void init() {
        mapDao    = new UserServerMapDAO();
        serverDao = new ServerDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ── 1. Session guard ────────────────────────────────────────────────
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        int userId = (int) session.getAttribute("userId");

        // ── 2. Parse server ID ───────────────────────────────────────────────
        String serverIdParam = req.getParameter("serverId");
        if (serverIdParam == null || serverIdParam.isEmpty()) {
            resp.sendRedirect("serverList.jsp?error=Missing+Server+ID");
            return;
        }

        try {
            int serverId = Integer.parseInt(serverIdParam);

            // ── 3. Load the server to know if it's password-protected ────────
            Server server = serverDao.getServerById(serverId);
            if (server == null) {
                resp.sendRedirect("serverList.jsp?error=Server+not+found");
                return;
            }

            // ── 4. Check if already a member ─────────────────────────────────
            if (mapDao.isUserInServer(userId, serverId)) {
                resp.sendRedirect("channelView.jsp?serverId=" + serverId);
                return;
            }

            // ── 5. Password validation for protected servers ─────────────────
            if (server.isPasswordProtected()) {
                String submittedPassword = req.getParameter("serverPassword");
                if (submittedPassword == null || submittedPassword.trim().isEmpty()) {
                    resp.sendRedirect("serverList.jsp?error=This+server+requires+a+password");
                    return;
                }
                // BCrypt check — verifyServerPassword re-fetches the hash safely
                boolean passwordOk = serverDao.verifyServerPassword(serverId, submittedPassword.trim());
                if (!passwordOk) {
                    resp.sendRedirect("serverList.jsp?error=Incorrect+server+password");
                    return;
                }
            }

            // ── 6. All checks passed — join the server ───────────────────────
            mapDao.joinServer(userId, serverId, null);
            resp.sendRedirect("channelView.jsp?serverId=" + serverId);

        } catch (NumberFormatException e) {
            resp.sendRedirect("serverList.jsp?error=Invalid+Server+ID");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("serverList.jsp?error=System+Error");
        }
    }
}
