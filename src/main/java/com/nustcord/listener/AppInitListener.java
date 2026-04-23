package com.nustcord.listener;

import com.nustcord.util.DBConnection;
import com.nustcord.util.PasswordUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing Database from schema.sql...");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
             
            // We use the absolute path to schema.sql here or real path
            String schemaPath = sce.getServletContext().getRealPath("/../schema.sql");
            if (schemaPath == null) {
                 schemaPath = "C:/Users/DELL/.gemini/antigravity/scratch/NUSTcord/schema.sql";
            }
             
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(schemaPath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                        sqlBuilder.append(line).append("\n");
                    }
                }
            }
            
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
            System.out.println("Database schema initialized successfully.");
            
            // Insert dummy user
            insertDummyUser(conn);
            
        } catch (Exception e) {
            System.err.println("Failed to initialize database schema.");
            e.printStackTrace();
        }
    }

    private void insertDummyUser(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'dummy'";
        String insertSql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        try (Statement checkStmt = conn.createStatement();
             java.sql.ResultSet rs = checkStmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // User doesn't exist, create it
                try (PreparedStatement pt = conn.prepareStatement(insertSql)) {
                    pt.setString(1, "dummy");
                    pt.setString(2, "dummy@example.com");
                    pt.setString(3, PasswordUtil.hashPassword("dummy"));
                    pt.executeUpdate();
                    System.out.println("Dummy account created: dummy / dummy");
                }
            } else {
                System.out.println("Dummy account already exists.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to create dummy user.");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup resources if necessary
    }
}
