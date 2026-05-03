package com.nustcord.servlet;

import com.nustcord.dao.ServerDAO;
import com.nustcord.dao.UserServerMapDAO;
import com.nustcord.model.Server;
import com.nustcord.service.ServerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/server")
public class ServerServlet extends HttpServlet {
    private ServerService serverService;

    @Override
    public void init() {
        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
        serverService = new ServerService(new ServerDAO(conn), new UserServerMapDAO(conn));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession(false);
        int userId = (int) session.getAttribute("userId");

        try {
            if ("create".equals(action)) {
                String name = req.getParameter("name");
                Server server = new Server();
                server.setName(name);
                serverService.createServer(server, userId);
                resp.sendRedirect("serverList.jsp");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
