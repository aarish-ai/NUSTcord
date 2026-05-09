<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Messages</title>
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
                    <i class="fas fa-comment-dots" style="margin-right: 10px; color: var(--text-muted);"></i> Direct Messages
                </div>
                <div class="content-body">
                    <%
                        com.nustcord.dao.DirectMessageDAO dmDao = new com.nustcord.dao.DirectMessageDAO();
                        java.util.List<com.nustcord.model.DirectMessage> conversations = dmDao.getRecentConversations(userId);
                        com.nustcord.dao.UserDAO uDao = new com.nustcord.dao.UserDAO();
                        
                        if (conversations.isEmpty()) {
                    %>
                        <div style="display:flex; justify-content:center; align-items:center; flex-direction:column; height:100%;">
                            <i class="fas fa-envelope-open-text" style="font-size: 64px; color: var(--bg-hover); margin-bottom: 20px;"></i>
                            <h2 style="color: var(--text-muted); margin-bottom: 10px;">No Active Messages</h2>
                            <p style="color: var(--text-muted); text-align: center; max-width: 400px;">
                                You haven't started any direct messages yet. Go to your <a href="friends.jsp" style="color: var(--accent-purple);">Friends list</a> to start chatting with someone directly!
                            </p>
                        </div>
                    <% } else { %>
                        <h3 class="category-title" style="margin-bottom: 15px;">Recent Conversations</h3>
                        <div style="display: flex; flex-direction: column; gap: 10px;">
                            <% for (com.nustcord.model.DirectMessage msg : conversations) {
                                int friendId = (msg.getSenderId() == userId) ? msg.getReceiverId() : msg.getSenderId();
                                com.nustcord.model.User friend = uDao.getUserById(friendId);
                                String friendName = (friend != null) ? friend.getUsername() : "Unknown User";
                            %>
                            <a href="directMessage?friendId=<%= friendId %>" class="list-item" style="padding: 15px; background-color: var(--bg-sidebar); border-radius: 8px; justify-content: flex-start; text-decoration: none;">
                                <div style="width: 40px; height: 40px; border-radius: 50%; background-color: var(--bg-hover); display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 16px; margin-right: 15px; color: var(--text-primary);">
                                    <%= friendName.substring(0, 1).toUpperCase() %>
                                </div>
                                <div style="flex: 1;">
                                    <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                                        <span style="font-weight: bold; color: var(--text-primary);"><%= friendName %></span>
                                        <span style="font-size: 12px; color: var(--text-muted);"><%= msg.getCreatedAt() %></span>
                                    </div>
                                    <div style="color: var(--text-muted); font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 400px;">
                                        <%= msg.getSenderId() == userId ? "You: " : "" %><%= msg.getContent() %>
                                    </div>
                                </div>
                            </a>
                            <% } %>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
