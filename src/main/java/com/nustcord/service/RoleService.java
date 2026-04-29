package com.nustcord.service;

import com.nustcord.dao.RoleDAO;
import com.nustcord.model.Role;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class RoleService {
    private RoleDAO roleDAO;

    public RoleService(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    public void createRole(Role role) throws SQLException {
        roleDAO.createRole(role);
    }

    public List<Role> getRoles(int serverId) throws SQLException {
        return roleDAO.getRolesByServer(serverId);
    }

    public boolean hasPermission(Role role, String permission) {
        if (role.getPermissions() == null) return false;
        return Arrays.asList(role.getPermissions().split(",")).contains(permission);
    }
}
