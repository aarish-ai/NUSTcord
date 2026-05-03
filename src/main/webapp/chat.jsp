<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.Message" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }

    List<Message> messages = (List<Message>) request.getAttribute("messages");
    int channelId = Integer.parseInt(request.getParameter("channelId"));
%>
<!DOCTYPE html>
<html>
<head>
    <title>Channel Chat</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container container-large">
        <h2>Channel Chat</h2>

        <div style="background: #2a2a30; padding: 15px; border-radius: 5px; margin-bottom: 20px; max-height: 400px; overflow-y: auto;">
            <% if(messages != null) { 
                for(Message msg : messages) { %>
                    <p><strong>User <%= msg.getSenderId() %>:</strong> <%= msg.getContent() %> 
                    <span style="color: gray; font-size: small;">[<%= msg.getCreatedAt() %>]</span></p>
            <% } } else { %>
                <p>No messages yet.</p>
            <% } %>
        </div>

        <form action="message" method="post" style="display: flex; gap: 10px;">
            <input type="hidden" name="channelId" value="<%= channelId %>"/>
            <input type="text" name="content" placeholder="Type your message..." style="flex-grow: 1;"/>
            <button type="submit" class="btn">Send</button>
        </form>

        <p><a href="channelView.jsp?serverId=<%= request.getParameter("serverId") %>">Back to Channels</a></p>
    </div>
</body>
</html>
