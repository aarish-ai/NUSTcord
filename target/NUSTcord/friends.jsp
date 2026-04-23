<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Friends</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container container-large">
        <div class="nav">
            <span style="font-weight: bold; color: var(--accent-purple);">NUSTcord</span>
            <div>
                <a href="dashboard.jsp">Dashboard</a> | 
                <a href="profile.jsp">Profile</a> | 
                <a href="friends.jsp">Friends</a> | 
                <a href="LogoutServlet">Logout</a>
            </div>
        </div>

        <h2>Friends System</h2>
        
        <% if (request.getParameter("error") != null) { %>
            <div class="message error"><%= request.getParameter("error") %></div>
        <% } %>
        <% if (request.getParameter("success") != null) { %>
            <div class="message success"><%= request.getParameter("success") %></div>
        <% } %>

        <div style="background: #2a2a30; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
            <h3>Send Friend Request</h3>
            <form action="FriendServlet" method="POST" style="display: flex; gap: 10px;">
                <input type="hidden" name="action" value="send">
                <input type="number" name="receiverId" placeholder="Receiver User ID" required style="width: auto; flex-grow: 1;">
                <button type="submit" class="btn" style="width: auto; margin-top: 0;">Send Request</button>
            </form>
        </div>
        
<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.FriendRequest" %>
<%@ page import="com.nustcord.dao.FriendRequestDAO" %>
<%@ page import="com.nustcord.dao.UserDAO" %>
<%@ page import="com.nustcord.model.User" %>

        <div style="background: #2a2a30; padding: 15px; border-radius: 5px;">
            <h3>Incoming Requests</h3>
            <% 
                Integer userId = (Integer) session.getAttribute("userId");
                if (userId != null) {
                    FriendRequestDAO reqDao = new FriendRequestDAO();
                    UserDAO userDao = new UserDAO();
                    List<FriendRequest> requests = reqDao.getPendingRequestsByReceiver(userId);
                    
                    if (requests.isEmpty()) {
            %>
                        <p style="color: var(--text-secondary); font-size: 0.9em;">No incoming requests.</p>
            <%      } else {
                        for (FriendRequest req : requests) {
                            User senderUser = userDao.getUserById(req.getSenderId());
                            String senderName = (senderUser != null) ? senderUser.getUsername() : "Unknown User";
            %>
                            <div style="display: flex; align-items: center; justify-content: space-between; padding: 10px; border-bottom: 1px solid #444;">
                                <span>Request from <strong><%= senderName %></strong> (ID: <%= req.getSenderId() %>)</span>
                                <div>
                                    <form action="FriendServlet" method="POST" style="display: inline-block;">
                                       <input type="hidden" name="action" value="accept">
                                       <input type="hidden" name="requestId" value="<%= req.getId() %>"> 
                                       <button type="submit" class="btn" style="background: var(--success-green); width: auto; padding: 5px 10px;">Accept</button>
                                    </form>
                                    <form action="FriendServlet" method="POST" style="display: inline-block; margin-left: 5px;">
                                       <input type="hidden" name="action" value="reject">
                                       <input type="hidden" name="requestId" value="<%= req.getId() %>">
                                       <button type="submit" class="btn" style="background: var(--error-red); width: auto; padding: 5px 10px;">Reject</button>
                                    </form>
                                </div>
                            </div>
            <%          }
                    }
                }
            %>
        </div>
        
        <div style="background: #2a2a30; padding: 15px; border-radius: 5px; margin-top: 20px;">
            <h3>My Friends</h3>
            <% 
                if (userId != null) {
                    com.nustcord.dao.FriendsDAO fdao = new com.nustcord.dao.FriendsDAO();
                    List<com.nustcord.model.Friend> friends = fdao.getFriendsForUser(userId);
                    if (friends.isEmpty()) {
            %>
                        <p style="color: var(--text-secondary); font-size: 0.9em;">No friends yet.</p>
            <%      } else {
                        UserDAO userDao = new UserDAO();
            %>
                        <ul style="list-style: none; padding: 0;">
            <%
                        for (com.nustcord.model.Friend f : friends) {
                            int friendId = (f.getUserId1() == userId) ? f.getUserId2() : f.getUserId1();
                            User friendUser = userDao.getUserById(friendId);
                            String friendName = (friendUser != null) ? friendUser.getUsername() : "Unknown User";
            %>
                            <li style="padding: 10px; border-bottom: 1px solid #444;">
                                <strong><%= friendName %></strong> (ID: <%= friendId %>)
                            </li>
            <%          }
            %>
                        </ul>
            <%      
                    }
                }
            %>
        </div>
    </div>
</body>
</html>
