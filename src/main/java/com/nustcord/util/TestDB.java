package com.nustcord.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Initializing Database from add_dm_table.sql...");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
             
            String schemaPath = "C:/Users/DELL/.gemini/antigravity/scratch/NUSTcord/add_dm_table.sql";
             
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
                    System.out.println("Executing: " + sql.trim());
                    stmt.execute(sql.trim());
                }
            }
            System.out.println("DM table added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
