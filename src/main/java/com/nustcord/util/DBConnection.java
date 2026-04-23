package com.nustcord.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // H2 Embedded File-Based Database in MySQL compatibility mode
    // Using an explicit path to make it easy to find for inspection
    private static final String URL = "jdbc:h2:file:C:/Users/DELL/.gemini/antigravity/scratch/NUSTcord/nustcord_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    static {
        try {
            // Load H2 Driver
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
