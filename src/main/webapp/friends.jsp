<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.FriendRequest" %>
<%@ page import="com.nustcord.dao.FriendRequestDAO" %>
<%@ page import="com.nustcord.dao.UserDAO" %>
<%@ page import="com.nustcord.model.User" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Friends</title>
    <link rel="stylesheet" type="text/css" href="css/style.css?v=2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="includes/top-nav.jsp" />
        
        <div class="main-wrapper">
            <jsp:include page="includes/left-sidebar.jsp" />

            <div class="main-content">
            <div class="main-header">
                <span style="font-weight: bold; margin-right: 20px;">Friends</span>
            </div>
            <div class="content-body">
                <% if (request.getParameter("error") != null) { %>
                    <div class="message-alert error"><%= request.getParameter("error") %></div>
                <% } %>
                <% if (request.getParameter("success") != null) { %>
                    <div class="message-alert success"><%= request.getParameter("success") %></div>
                <% } %>

                <div class="card" style="display: flex; gap: 10px; padding: 15px;">
                    <form action="FriendServlet" method="POST" style="display: flex; width: 100%; gap: 10px;">
                        <input type="hidden" name="action" value="send">
                        <input type="text" name="receiverUsername" placeholder="You can add a friend with their username." required style="flex-grow: 1;">
                        <button type="submit" class="btn btn-sm">Send Friend Request</button>
                    </form>
                </div>

                <div style="display: flex; gap: 20px;">
                    <!-- Left Column: Friends List -->
                    <div style="flex: 1;">
                        <h3 class="category-title" style="margin-left: 0;">All Friends</h3>
                        <div class="card" style="padding: 10px;">
                            <% 
                                com.nustcord.dao.FriendsDAO fdao = new com.nustcord.dao.FriendsDAO();
                                List<com.nustcord.model.Friend> friends = fdao.getFriendsForUser(userId);
                                UserDAO userDao = new UserDAO();
                                if (friends.isEmpty()) {
                            %>
                                <div style="padding: 20px; text-align: center; color: var(--text-muted);">
                                    You don't have any friends yet. Add some!
                                </div>
                            <% } else { %>
                                <div style="display: flex; flex-direction: column; gap: 5px;">
                                <%
                                    for (com.nustcord.model.Friend f : friends) {
                                        int friendId = (f.getUserId1() == userId) ? f.getUserId2() : f.getUserId1();
                                        User friendUser = userDao.getUserById(friendId);
                                        String friendName = (friendUser != null) ? friendUser.getUsername() : "Unknown User";
                                %>
                                    <div class="list-item" style="justify-content: space-between; padding: 10px; cursor: default;">
                                        <div style="display: flex; align-items: center; gap: 10px;">
                                            <div style="width: 32px; height: 32px; border-radius: 50%; background-color: var(--bg-hover); display: flex; align-items: center; justify-content: center; font-weight: bold;"><%= friendName.substring(0, 1).toUpperCase() %></div>
                                            <span><%= friendName %></span>
                                        </div>
                                        <a href="directMessage?friendId=<%= friendId %>" class="btn btn-sm" style="background-color: var(--bg-base); border: 1px solid var(--bg-hover); padding: 6px 10px;" title="Message"><i class="fas fa-comment"></i></a>
                                    </div>
                                <%  } %>
                                </div>
                            <% } %>
                        </div>
                    </div>

                    <!-- Right Column: Pending Requests -->
                    <div style="flex: 1;">
                        <h3 class="category-title" style="margin-left: 0;">Pending Requests</h3>
                        <div class="card" style="padding: 10px;">
                            <% 
                                FriendRequestDAO reqDao = new FriendRequestDAO();
                                List<FriendRequest> requests = reqDao.getPendingRequestsByReceiver(userId);
                                if (requests.isEmpty()) {
                            %>
                                <div style="padding: 20px; text-align: center; color: var(--text-muted);">
                                    No incoming requests.
                                </div>
                            <% } else { %>
                                <div style="display: flex; flex-direction: column; gap: 5px;">
                                <%
                                    for (FriendRequest req : requests) {
                                        User senderUser = userDao.getUserById(req.getSenderId());
                                        String senderName = (senderUser != null) ? senderUser.getUsername() : "Unknown User";
                                %>
                                    <div class="list-item" style="justify-content: space-between; padding: 10px; cursor: default; background-color: var(--bg-base);">
                                        <div style="display: flex; align-items: center; gap: 10px;">
                                            <div style="width: 32px; height: 32px; border-radius: 50%; background-color: var(--bg-hover); display: flex; align-items: center; justify-content: center; font-weight: bold;"><%= senderName.substring(0, 1).toUpperCase() %></div>
                                            <span><%= senderName %></span>
                                        </div>
                                        <div style="display: flex; gap: 5px;">
                                            <form action="FriendServlet" method="POST">
                                                <input type="hidden" name="action" value="accept">
                                                <input type="hidden" name="requestId" value="<%= req.getId() %>"> 
                                                <button type="submit" class="btn btn-sm btn-success">✓</button>
                                            </form>
                                            <form action="FriendServlet" method="POST">
                                                <input type="hidden" name="action" value="reject">
                                                <input type="hidden" name="requestId" value="<%= req.getId() %>">
                                                <button type="submit" class="btn btn-sm btn-danger">✕</button>
                                            </form>
                                        </div>
                                    </div>
                                <%  } %>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

