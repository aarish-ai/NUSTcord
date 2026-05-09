package com.nustcord.servlet;

import com.nustcord.dao.UserServerMapDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/joinServer")
public class JoinServerServlet extends HttpServlet {
    private UserServerMapDAO mapDao;

    @Override
    public void init() {
        mapDao = new UserServerMapDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String serverIdParam = req.getParameter("serverId");

        if (serverIdParam == null || serverIdParam.isEmpty()) {
            resp.sendRedirect("serverList.jsp?error=Missing+Server+ID");
            return;
        }

        try {
            int serverId = Integer.parseInt(serverIdParam);
            
            // Check if already joined
            if (mapDao.isUserInServer(userId, serverId)) {
                resp.sendRedirect("serverList.jsp?error=Already+Joined");
                return;
            }

            try {
                mapDao.joinServer(userId, serverId, null);
                resp.sendRedirect("channelView.jsp?serverId=" + serverId);
            } catch (Exception e) {
                resp.sendRedirect("serverList.jsp?error=Failed+to+Join");
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect("serverList.jsp?error=Invalid+Server+ID");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("serverList.jsp?error=System+Error");
        }
    }
}
