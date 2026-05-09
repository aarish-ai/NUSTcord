<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.DirectMessage" %>
<%@ page import="com.nustcord.dao.UserDAO" %>
<%@ page import="com.nustcord.model.User" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }

    String friendIdParam = request.getParameter("friendId");
    if (friendIdParam == null || friendIdParam.isEmpty()) {
        response.sendRedirect("friends.jsp");
        return;
    }
    
    int friendId = 0;
    try {
        friendId = Integer.parseInt(friendIdParam);
    } catch (NumberFormatException e) {
        response.sendRedirect("friends.jsp");
        return;
    }

    List<DirectMessage> messages = (List<DirectMessage>) request.getAttribute("messages");
    if (messages == null) {
        // Redirect to servlet to fetch messages if accessed directly
        response.sendRedirect("directMessage?friendId=" + friendId);
        return;
    }
    
    UserDAO uDao = new UserDAO();
    User friendUser = uDao.getUserById(friendId);
    String friendName = (friendUser != null) ? friendUser.getUsername() : "Unknown User";
    
    User myUser = uDao.getUserById(userId);
    String myName = (myUser != null) ? myUser.getUsername() : "Me";
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Message @<%= friendName %></title>
    <link rel="stylesheet" type="text/css" href="css/style.css?v=2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script>
        window.onload = function() {
            var chatBox = document.getElementById("chat-messages");
            if (chatBox) chatBox.scrollTop = chatBox.scrollHeight;
        };
    </script>
</head>
<body>
    <div class="app-container">
        <jsp:include page="includes/top-nav.jsp" />
        
        <div class="main-wrapper">
            <jsp:include page="includes/left-sidebar.jsp" />

            <div class="main-content">
                <div class="main-header" style="border-bottom: 1px solid var(--bg-card);">
                    <i class="fas fa-at" style="color: var(--text-muted); margin-right: 8px; font-size: 16px;"></i>
                    <span><%= friendName %></span>
                </div>
                
                <div class="chat-messages" id="chat-messages">
                    <% if(messages != null && !messages.isEmpty()) { 
                        for(DirectMessage msg : messages) { 
                            boolean isMine = (msg.getSenderId() == userId);
                            String authorName = isMine ? myName : friendName;
                    %>
                            <div class="message" style="<%= isMine ? "border-left: 3px solid var(--success-green);" : "" %>">
                                <div class="message-header">
                                    <span class="message-author" style="<%= isMine ? "color: var(--success-green);" : "" %>"><%= authorName %></span>
                                    <span class="message-timestamp"><%= msg.getCreatedAt() %></span>
                                </div>
                                <div class="message-content"><%= msg.getContent() %></div>
                            </div>
                    <%  } 
                       } else { %>
                        <div style="flex: 1; display: flex; align-items: flex-end;">
                            <div style="margin-bottom: 20px;">
                                <div style="width: 80px; height: 80px; border-radius: 50%; background-color: var(--bg-hover); display: flex; align-items: center; justify-content: center; font-size: 32px; font-weight: bold; margin-bottom: 15px;"><%= friendName.substring(0, 1).toUpperCase() %></div>
                                <h2 style="color: var(--text-primary); margin-bottom: 5px;"><%= friendName %></h2>
                                <p style="color: var(--text-muted);">This is the beginning of your direct message history with <strong>@<%= friendName %></strong>.</p>
                            </div>
                        </div>
                    <% } %>
                </div>

                <div class="chat-input-wrapper">
                    <form action="directMessage" method="post" class="chat-input-container">
                        <input type="hidden" name="friendId" value="<%= friendId %>"/>
                        <input type="text" name="content" class="chat-input" placeholder="Message @<%= friendName %>" required autocomplete="off" autofocus/>
                        <button type="submit" style="display: none;">Send</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
