<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.Message" %>
<%@ page import="com.nustcord.model.Channel" %>
<%@ page import="com.nustcord.service.ChannelService" %>
<%@ page import="com.nustcord.dao.ChannelDAO" %>
<%@ page import="com.nustcord.service.ProfileService" %>
<%@ page import="com.nustcord.model.Profile" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }

    int serverId = Integer.parseInt(request.getParameter("serverId"));
    int channelId = Integer.parseInt(request.getParameter("channelId"));

    // Need channels for the sidebar
    ChannelService channelService = new ChannelService(new ChannelDAO());
    List<Channel> channels = channelService.getChannels(serverId);
    Channel activeChannel = null;
    for (Channel c : channels) {
        if (c.getId() == channelId) { activeChannel = c; break; }
    }

    List<Message> messages = (List<Message>) request.getAttribute("messages");
    if (messages == null) {
        // If direct access, redirect to servlet to fetch messages
        response.sendRedirect("message?channelId=" + channelId + "&serverId=" + serverId);
        return;
    }
    
    ProfileService ps = new ProfileService();
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Chat</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <script>
        // Auto-scroll to bottom of chat
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
                <span style="color: var(--text-muted); margin-right: 8px; font-size: 20px;">#</span>
                <span><%= activeChannel != null ? activeChannel.getName() : "Channel" %></span>
            </div>
            
            <div class="chat-messages" id="chat-messages">
                <% if(messages != null && !messages.isEmpty()) { 
                    for(Message msg : messages) { 
                        Profile p = ps.getProfile(msg.getSenderId());
                        String authorName = (p != null && p.getDisplayName() != null) ? p.getDisplayName() : "User " + msg.getSenderId();
                %>
                        <div class="message">
                            <div class="message-header">
                                <span class="message-author"><%= authorName %></span>
                                <span class="message-timestamp"><%= msg.getCreatedAt() %></span>
                            </div>
                            <div class="message-content"><%= msg.getContent() %></div>
                        </div>
                <%  } 
                   } else { %>
                    <div style="flex: 1; display: flex; align-items: flex-end;">
                        <h2 style="color: var(--text-muted); margin-bottom: 0;">Welcome to #<%= activeChannel != null ? activeChannel.getName() : "this channel" %>!</h2>
                    </div>
                    <p style="color: var(--text-muted);">This is the start of the channel history.</p>
                <% } %>
            </div>

            <div class="chat-input-wrapper">
                <form action="message" method="post" class="chat-input-container">
                    <input type="hidden" name="channelId" value="<%= channelId %>"/>
                    <input type="hidden" name="serverId" value="<%= serverId %>"/>
                    <input type="text" name="content" class="chat-input" placeholder="Message #<%= activeChannel != null ? activeChannel.getName() : "" %>" required autocomplete="off"/>
                </form>
            </div>
            </div>
        </div>
    </div>
</body>
</html>
