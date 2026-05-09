<%@ page import="com.nustcord.model.Channel" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.service.ChannelService" %>
<%@ page import="com.nustcord.dao.ChannelDAO" %>
<%
    String serverIdParam = request.getParameter("serverId");
    String channelIdParam = request.getParameter("channelId");
%>
<div class="left-sidebar">
    <div class="sidebar-section" style="border-bottom: 1px solid var(--bg-card); padding-bottom: 20px;">
        <h3 class="category-title">Direct Messages</h3>
        <a href="dashboard.jsp" class="list-item">
            <span style="margin-right: 10px;">🏠</span> Home
        </a>
        <a href="friends.jsp" class="list-item">
            <span style="margin-right: 10px;">👋</span> Friends
        </a>
        <a href="#" class="list-item">
            <span style="margin-right: 10px;">💬</span> Messages
        </a>
    </div>

    <div class="sidebar-section" style="flex: 1; overflow-y: auto;">
        <% if (serverIdParam != null && !serverIdParam.isEmpty()) { 
            int sid = Integer.parseInt(serverIdParam);
            ChannelService cService = new ChannelService(new ChannelDAO());
            List<Channel> channels = cService.getChannels(sid);
        %>
            <div class="flex-between" style="margin-bottom: 10px;">
                <h3 class="category-title" style="margin: 0;">Channels</h3>
                <a href="serverSettings.jsp?serverId=<%=sid%>" style="color: var(--text-muted); text-decoration: none;" title="Server Settings">⚙️</a>
            </div>
            <% for(Channel c : channels) { 
                String isActive = (channelIdParam != null && channelIdParam.equals(String.valueOf(c.getId()))) ? "active" : "";
            %>
                <a href="chat.jsp?serverId=<%= sid %>&channelId=<%= c.getId() %>" class="list-item <%= isActive %>">
                    <span style="color: var(--text-muted); margin-right: 8px;">#</span> <%= c.getName() %>
                </a>
            <% } %>
            
            <!-- Quick Add Channel -->
            <form action="channel" method="post" style="margin-top: 15px; border-top: 1px solid var(--bg-card); padding-top: 15px;">
                <input type="hidden" name="serverId" value="<%=sid%>"/>
                <input type="text" name="name" placeholder="New channel..." required style="margin-bottom: 8px; font-size: 12px; padding: 8px; background-color: var(--bg-hover);">
                <input type="hidden" name="type" value="TEXT"/>
                <button type="submit" class="btn btn-sm btn-block" style="background-color: var(--bg-hover); color: var(--text-primary); border: 1px solid var(--bg-card);">+ Add</button>
            </form>
        <% } else { %>
            <div style="text-align: center; padding: 20px 10px; color: var(--text-muted); font-size: 13px;">
                <p>Select a server from the top menu to view its channels.</p>
                <a href="serverList.jsp" class="btn btn-sm" style="margin-top: 10px;">View Servers</a>
            </div>
        <% } %>
    </div>
</div>
