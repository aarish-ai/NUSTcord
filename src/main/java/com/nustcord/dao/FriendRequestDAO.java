package com.nustcord.dao;

/**
 * FriendRequestDAO.java
 * Purpose: Data Access Object for the friend request workflow.
 * Key Responsibilities:
 *  - Create new pending friend requests between two users
 *  - Update the status of a request (ACCEPTED, REJECTED, CANCELLED)
 *  - Fetch a single request by ID (used when accepting/rejecting)
 *  - List all pending requests received by a user (inbox)
 *  - List all requests sent by a user (outbox / sent requests)
 * Created: 2026-05-12
 */

import com.nustcord.model.FriendRequest;
import com.nustcord.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages persistence for the "friend_requests" table.
 * Pairs with FriendsDAO (which handles the accepted "friends" table)
 * and FriendService (which orchestrates the two-step accept flow).
 */
public class FriendRequestDAO {

    /**
     * Inserts a new friend request with PENDING status.
     * The database has a UNIQUE KEY on (sender_id, receiver_id) so duplicate
     * requests will silently fail and return false.
     *
     * @param senderId   The user ID of the person sending the request.
     * @param receiverId The user ID of the intended recipient.
     * @return true if the INSERT succeeded, false on any error or duplicate.
     */
    public boolean createRequest(int senderId, int receiverId) {
        // Status defaults to PENDING; the database column has a DEFAULT too
        String sql = "INSERT INTO friend_requests (sender_id, receiver_id, status) VALUES (?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // Likely a duplicate-key violation – log and return false
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the status field of an existing friend request.
     * Called by FriendService when a user accepts, rejects, or cancels a request.
     *
     * @param requestId The primary key of the friend_requests row to update.
     * @param status    New status string: "ACCEPTED", "REJECTED", or "CANCELLED".
     * @return true if the UPDATE affected one row, false otherwise.
     */
    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE friend_requests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind status first, then the ID used in the WHERE clause
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a single FriendRequest by its primary key.
     * Used before accepting/rejecting to verify the request exists and to
     * extract the sender/receiver IDs needed for further operations.
     *
     * @param requestId The primary key of the friend request to fetch.
     * @return A FriendRequest object if found, or null if no such ID exists.
     */
    public FriendRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM friend_requests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Map the row columns to a FriendRequest model object
                    FriendRequest r = new FriendRequest();
                    r.setId(rs.getInt("id"));
                    r.setSenderId(rs.getInt("sender_id"));
                    r.setReceiverId(rs.getInt("receiver_id"));
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Return null signals "request not found" to the calling service
        return null;
    }

    /**
     * Returns all PENDING friend requests where the given user is the receiver.
     * This is the user's "incoming request inbox" shown on friends.jsp.
     *
     * @param receiverId The user ID to find pending requests for.
     * @return A List of FriendRequest objects (may be empty if no pending requests).
     */
    public List<FriendRequest> getPendingRequestsByReceiver(int receiverId) {
        List<FriendRequest> requests = new ArrayList<>();
        // Only return PENDING requests; accepted/rejected ones are no longer actionable
        String sql = "SELECT * FROM friend_requests WHERE receiver_id = ? AND status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, receiverId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FriendRequest r = new FriendRequest();
                    r.setId(rs.getInt("id"));
                    r.setSenderId(rs.getInt("sender_id"));
                    r.setReceiverId(rs.getInt("receiver_id"));
                    r.setStatus(rs.getString("status"));
                    requests.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * Returns all friend requests sent by the given user, in any status.
     * Used to show the "Sent Requests" dropdown on friends.jsp so users
     * can see which requests are still pending, accepted, or rejected.
     *
     * @param senderId The user ID whose outgoing requests we want.
     * @return A List of FriendRequest objects representing all sent requests.
     */
    public List<FriendRequest> getRequestsBySender(int senderId) {
        List<FriendRequest> requests = new ArrayList<>();
        // Retrieve all statuses so the UI can show PENDING, ACCEPTED, and REJECTED
        String sql = "SELECT * FROM friend_requests WHERE sender_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, senderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FriendRequest r = new FriendRequest();
                    r.setId(rs.getInt("id"));
                    r.setSenderId(rs.getInt("sender_id"));
                    r.setReceiverId(rs.getInt("receiver_id"));
                    r.setStatus(rs.getString("status"));
                    requests.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
}
