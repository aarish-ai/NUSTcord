package com.nustcord.util;

/**
 * DBConnection.java
 * Purpose: Provides a centralised, reusable database connection factory.
 * Key Responsibilities:
 *  - Store the JDBC connection URL, username, and password as constants
 *  - Load the H2 database driver once via a static initializer block
 *  - Expose a single static getConnection() method used by all DAO classes
 * Created: 2026-05-12
 *
 * NOTE: This project uses an H2 embedded file-based database in MySQL
 * compatibility mode. In a production deployment you would replace this
 * with a connection pool (e.g., HikariCP + real MySQL/PostgreSQL).
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for obtaining JDBC database connections.
 * All DAO classes call DBConnection.getConnection() inside try-with-resources
 * blocks to ensure connections are always closed after use.
 *
 * This class is not meant to be instantiated – all members are static.
 */
public class DBConnection {

    // H2 Embedded File-Based Database in MySQL compatibility mode.
    // DATABASE_TO_LOWER=TRUE makes column names case-insensitive (matches MySQL behaviour).
    // AUTO_SERVER=TRUE allows multiple JVM processes to share the same file database.
    private static final String URL =
        "jdbc:h2:file:C:/Users/DELL/.gemini/antigravity/scratch/NUSTcord/nustcord_db;" +
        "MODE=MySQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE";

    // H2 default credentials (no real security needed for embedded dev DB)
    private static final String USER     = "root";
    private static final String PASSWORD = "password";

    // Static initializer: runs once when the class is first loaded by the JVM.
    // This guarantees the H2 driver is registered before any getConnection() call.
    static {
        try {
            // Load H2 JDBC Driver – required for DriverManager to find the connection
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            // This would only happen if the H2 JAR is missing from the classpath
            System.err.println("Database driver not found: " + e.getMessage());
        }
    }

    /**
     * Opens and returns a new JDBC connection to the H2 database.
     * Callers MUST close the returned Connection (preferably via try-with-resources)
     * to avoid connection leaks.
     *
     * @return A live, open Connection ready for SQL operations.
     * @throws SQLException if the driver cannot establish a connection.
     */
    public static Connection getConnection() throws SQLException {
        // DriverManager creates a new physical connection each time.
        // For high-traffic production use, replace with a connection pool.
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
