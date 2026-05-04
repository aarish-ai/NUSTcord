package com.nustcord.dao;

import com.nustcord.model.Role;
import java.sql.*;
import java.util.*;

public class RoleDAO {
    private Connection conn;

    public RoleDAO(Connection conn) {
        this.conn = conn;
    }

    public void createRole(Role role) throws SQLException {
        String sql = "INSERT INTO roles (server_id, name, permissions) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, role.getServerId());
            stmt.setString(2, role.getName());
            stmt.setString(3, role.getPermissions());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    role.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Role> getRolesByServer(int serverId) throws SQLException {
        String sql = "SELECT * FROM roles WHERE server_id = ?";
        List<Role> roles = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setServerId(rs.getInt("server_id"));
                    role.setName(rs.getString("name"));
                    role.setPermissions(rs.getString("permissions"));
                    roles.add(role);
                }
            }
        }
        return roles;
    }
}
