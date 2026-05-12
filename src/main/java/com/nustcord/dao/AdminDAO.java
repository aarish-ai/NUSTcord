package com.nustcord.dao;

/**
 * AdminDAO.java
 * Purpose: Data Access Object for administrative operations.
 * Key Responsibilities:
 *  - Fetch all users from the database with aggregated statistics
 *  - Aggregate friend counts, server memberships, and status per user
 *  - Provide the data model for the admin dashboard display
 * Created: 2026-05-12
 */

import com.nustcord.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides admin-only database queries that aggregate user data across
 * multiple tables for display on the admin dashboard.
 */
public class AdminDAO {

    /**
     * Represents a single row in the admin user table, holding all
     * statistics for one user.
     */
    public static class AdminUserRow {
        /** Primary key of the user in the users table */
        public int userId;
        /** Login username */
        public String username;
        /** Email address */
        public String email;
        /** Display name from the profiles table (may be null if not set) */
        public String displayName;
        /** Timestamp when the user registered */
        public Timestamp registeredAt;
        /** Current status string: Online, Offline, Busy, or Away */
        public String status;
        /** Total number of accepted friendships for this user */
        public int friendCount;
        /** Number of servers this user is a member of */
        public int serverCount;
        /** Timestamp of the user's most recent status update (used as last login proxy) */
        public Timestamp lastLogin;
    }

    /**
     * Fetches every user in the system along with aggregated statistics.
     * Uses LEFT JOINs so users with no friends, servers, or status still appear.
     *
     * The friend count aggregation accounts for the fact that friendships are
     * stored with the smaller user_id in user_id1, so we must count rows where
     * the user appears in either column.
     *
     * @return A List of AdminUserRow objects, one per user, ordered by user ID.
     * @throws SQLException if the database query fails.
     */
    public List<AdminUserRow> getAllUsersWithStats() throws SQLException {
        // This query joins users to profiles, status, friend counts, and server counts.
        // We use subqueries for friend count because a user can be in user_id1 OR user_id2.
        String sql =
            "SELECT " +
            "  u.id AS user_id, " +
            "  u.username, " +
            "  u.email, " +
            "  u.created_at AS registered_at, " +
            "  p.display_name, " +
            "  COALESCE(us.status, 'Offline') AS status, " +
            "  us.last_updated AS last_login, " +
            // Count servers via user_server_map
            "  (SELECT COUNT(*) FROM user_server_map usm WHERE usm.user_id = u.id) AS server_count, " +
            // Count friendships: user appears in either column of the friends table
            "  (SELECT COUNT(*) FROM friends f WHERE f.user_id1 = u.id OR f.user_id2 = u.id) AS friend_count " +
            "FROM users u " +
            "LEFT JOIN profiles p ON u.id = p.user_id " +
            "LEFT JOIN user_status us ON u.id = us.user_id " +
            "ORDER BY u.id ASC";

        List<AdminUserRow> rows = new ArrayList<>();

        // Use try-with-resources to guarantee connection and statement are closed
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Iterate through each returned row and map it to an AdminUserRow object
            while (rs.next()) {
                AdminUserRow row = new AdminUserRow();

                // Map basic user fields
                row.userId       = rs.getInt("user_id");
                row.username     = rs.getString("username");
                row.email        = rs.getString("email");
                row.registeredAt = rs.getTimestamp("registered_at");

                // Display name may be null if the user never set up a profile
                row.displayName  = rs.getString("display_name");
                if (row.displayName == null || row.displayName.isEmpty()) {
                    // Fall back gracefully so the JSP never shows "null"
                    row.displayName = "(not set)";
                }

                // Status defaults to Offline if user_status row doesn't exist (COALESCE above)
                row.status       = rs.getString("status");

                // last_login is the last_updated from user_status; may be null for new users
                row.lastLogin    = rs.getTimestamp("last_login");

                // Aggregated counts
                row.friendCount  = rs.getInt("friend_count");
                row.serverCount  = rs.getInt("server_count");

                rows.add(row);
            }
        }

        return rows;
    }
}
