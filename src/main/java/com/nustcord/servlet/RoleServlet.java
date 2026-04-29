package com.nustcord.servlet;

import com.nustcord.dao.RoleDAO;
import com.nustcord.model.Role;
import com.nustcord.service.RoleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/role")
public class RoleServlet extends HttpServlet {
    private RoleService roleService;

    @Override
    public void init() {
        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
        roleService = new RoleService(new RoleDAO(conn));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        int serverId = Integer.parseInt(req.getParameter("serverId"));
        String name = req.getParameter("name");
        String permissions = req.getParameter("permissions");

        Role role = new Role();
        role.setServerId(serverId);
        role.setName(name);
        role.setPermissions(permissions);

        try {
            roleService.createRole(role);
            resp.sendRedirect("serverSettings.jsp?serverId=" + serverId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
