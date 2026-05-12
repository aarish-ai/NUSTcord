package com.nustcord.listener;

/**
 * AppInitListener.java
 * Purpose: Servlet context lifecycle listener that runs on application startup.
 * Key Responsibilities:
 *  - Execute schema.sql to create all database tables (IF NOT EXISTS, so safe to re-run)
 *  - Insert a "dummy" test account if it doesn't already exist
 *  - Insert the "admin" account (username: admin, password: password1) if not already present
 *  - Log all actions so startup errors are easy to diagnose in the Tomcat console
 * Created: 2026-05-12
 */

import com.nustcord.util.DBConnection;
import com.nustcord.util.PasswordUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Runs once when Tomcat deploys the NUSTcord application.
 * Ensures the database schema exists and seed accounts are present.
 * Uses @WebListener so no web.xml entry is needed.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    /**
     * Called by Tomcat when the application context is first initialised.
     * Executes schema.sql to create tables (all CREATE TABLE IF NOT EXISTS),
     * then inserts required seed accounts.
     *
     * @param sce provides access to the servlet context (for getRealPath)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing Database from schema.sql...");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Resolve the absolute path to schema.sql on the filesystem.
            // getRealPath works inside the WAR; fall back to hardcoded path for IDE runs.
            String schemaPath = sce.getServletContext().getRealPath("/../schema.sql");
            if (schemaPath == null) {
                // Fallback path for running directly from the IDE (not deployed to Tomcat)
                schemaPath = "C:/Users/DELL/.gemini/antigravity/scratch/NUSTcord/schema.sql";
            }

            // Read the entire SQL file into a StringBuilder, skipping comment lines
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(schemaPath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // Skip SQL comment lines and blank lines to prevent parse errors
                    if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                        sqlBuilder.append(line).append("\n");
                    }
                }
            }

            // Split the file into individual statements on semicolons and execute each
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    // All CREATE TABLE statements use IF NOT EXISTS, so re-runs are safe
                    stmt.execute(sql.trim());
                }
            }
            System.out.println("Database schema initialized successfully.");

            // Seed the test dummy account (username: dummy, password: dummy)
            insertSeedUser(conn, "dummy", "dummy@example.com", "dummy");

            // Seed the admin account with the required credentials:
            // username: admin, password: password1
            insertSeedUser(conn, "admin", "admin@nustcord.local", "password1");

        } catch (Exception e) {
            System.err.println("Failed to initialize database schema.");
            e.printStackTrace();
        }
    }

    /**
     * Inserts a seed user account if one with the given username doesn't already exist.
     * Uses BCrypt hashing (via PasswordUtil) so the credential works with AuthService.login().
     *
     * @param conn         An open database connection (reused from the caller)
     * @param username     The login username to insert
     * @param email        The email address for this seed account
     * @param plainPassword The plain-text password (will be BCrypt-hashed before storage)
     */
    private void insertSeedUser(Connection conn, String username, String email, String plainPassword) {
        // Check if this username already exists to avoid duplicate key errors
        String checkSql  = "SELECT COUNT(*) FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);

            try (java.sql.ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // User doesn't exist – create it now with a BCrypt-hashed password
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, email);
                        // IMPORTANT: hash the password with BCrypt so AuthService can verify it
                        insertStmt.setString(3, PasswordUtil.hashPassword(plainPassword));
                        insertStmt.executeUpdate();
                        System.out.println("Seed account created: " + username);
                    }
                } else {
                    System.out.println("Seed account already exists: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to create seed user '" + username + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the application is undeployed or Tomcat shuts down.
     * No cleanup needed since DBConnection uses DriverManager (not a pool).
     *
     * @param sce the servlet context event (unused)
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No connection pool to shut down; H2 embedded DB closes with the JVM
    }
}
