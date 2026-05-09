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
        roleService = new RoleService(new RoleDAO());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String serverIdParam = req.getParameter("serverId");
        if (serverIdParam == null || serverIdParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing serverId");
            return;
        }
        int serverId;
        try {
            serverId = Integer.parseInt(serverIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid serverId format");
            return;
        }
        
        String name = req.getParameter("name");
        String permissions = req.getParameter("permissions");
        if (name == null || permissions == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing role parameters");
            return;
        }

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
