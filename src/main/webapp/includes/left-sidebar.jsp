<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.Channel" %>
<%@ page import="com.nustcord.service.ChannelService" %>
<%@ page import="com.nustcord.dao.ChannelDAO" %>
<div class="left-sidebar">
<%
    String serverIdParamStr = request.getParameter("serverId");
    String activeChannelIdStr = request.getParameter("channelId");
    
    if (serverIdParamStr != null && !serverIdParamStr.isEmpty()) {
        int sidebarServerId = 0;
        try { sidebarServerId = Integer.parseInt(serverIdParamStr); } catch (Exception e) {}
        if (sidebarServerId > 0) {
            ChannelService cs = new ChannelService(new ChannelDAO());
            List<Channel> channelsList = cs.getChannels(sidebarServerId);
%>
        <div class="sidebar-section" style="border-bottom: 1px solid var(--bg-card); padding-bottom: 20px;">
            <h3 class="category-title" style="display: flex; justify-content: space-between; align-items: center;">
                <span>SERVER CHANNELS</span>
                <a href="serverSettings.jsp?serverId=<%= sidebarServerId %>" title="Server Settings" style="color: var(--text-muted); text-decoration: none;"><i class="fas fa-cog"></i></a>
            </h3>
            <% if (channelsList != null) { 
                for (Channel c : channelsList) { 
                    String isActive = (activeChannelIdStr != null && activeChannelIdStr.equals(String.valueOf(c.getId()))) ? "active" : "";
            %>
                <a href="message?serverId=<%= sidebarServerId %>&channelId=<%= c.getId() %>" class="list-item <%= isActive %>">
                    <span style="color: var(--text-muted); margin-right: 8px; font-weight: bold;">#</span> <%= c.getName() %>
                </a>
            <%  }
               } %>
        </div>
<%
        }
    } else {
%>
    <div class="sidebar-section" style="border-bottom: 1px solid var(--bg-card); padding-bottom: 20px;">
        <h3 class="category-title">DIRECT MESSAGES</h3>
        <a href="dashboard.jsp" class="list-item">
            <i class="fas fa-home" style="width: 20px;"></i> Home
        </a>
        <a href="friends.jsp" class="list-item">
            <i class="fas fa-user-friends" style="width: 20px;"></i> Friends
        </a>
        <a href="messages.jsp" class="list-item">
            <i class="fas fa-comment-dots" style="width: 20px;"></i> Messages
        </a>
    </div>
<%
    }
%>
</div>
