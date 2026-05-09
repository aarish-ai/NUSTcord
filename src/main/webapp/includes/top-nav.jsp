<%@ page import="com.nustcord.model.UserStatus" %>
<%@ page import="com.nustcord.service.ProfileService" %>
<%@ page import="com.nustcord.model.Server" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.service.ServerService" %>
<%@ page import="com.nustcord.dao.ServerDAO" %>
<%@ page import="com.nustcord.dao.UserServerMapDAO" %>
<%
    Integer currentUserId = (Integer) session.getAttribute("userId");
    String currentUsername = (String) session.getAttribute("username");
    String uStatus = "Offline";
    List<Server> userServers = null;
    if (currentUserId != null) {
        ProfileService pService = new ProfileService();
        UserStatus statusObj = pService.getStatus(currentUserId);
        if (statusObj != null && statusObj.getStatus() != null) {
            uStatus = statusObj.getStatus();
        }
        ServerService srvService = new ServerService(new ServerDAO(), new UserServerMapDAO());
        userServers = srvService.getUserServers(currentUserId);
    }
%>
<div class="top-nav">
    <div class="brand">
        <i class="fas fa-gamepad" style="color: var(--accent-purple); margin-right: 8px;"></i>NUSTcord
    </div>
    
    <div style="display: flex; gap: 10px; align-items: center; margin-left: 30px; flex: 1;">
        <!-- Server List -->
        <% if (userServers != null) {
            for(Server s : userServers) { 
        %>
            <a href="channelView.jsp?serverId=<%=s.getId()%>" title="<%=s.getName()%>" style="width: 35px; height: 35px; border-radius: 50%; background-color: var(--bg-hover); color: var(--text-primary); display: flex; align-items: center; justify-content: center; text-decoration: none; font-weight: bold; font-size: 14px; transition: all 0.2s;">
                <%=s.getName().substring(0, Math.min(2, s.getName().length())).toUpperCase()%>
            </a>
        <%  }
        } %>
        <a href="serverList.jsp" title="Add a Server" style="width: 35px; height: 35px; border-radius: 50%; border: 1px dashed var(--success-green); color: var(--success-green); display: flex; align-items: center; justify-content: center; text-decoration: none; font-weight: bold; font-size: 18px; transition: all 0.2s;">
            <i class="fas fa-plus" style="font-size: 14px;"></i>
        </a>
    </div>

    <div class="nav-links">
        <a href="profile.jsp" class="nav-link" title="Profile"><i class="fas fa-user"></i></a>
        <a href="settings.jsp" class="nav-link" title="Settings"><i class="fas fa-cog"></i></a>
        
        <div style="display: flex; align-items: center; gap: 15px; margin-left: 10px; padding-left: 20px; border-left: 1px solid var(--bg-hover);">
            <div style="width: 35px; height: 35px; border-radius: 50%; background-color: var(--accent-purple); display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 16px;">
                <%= currentUsername != null ? currentUsername.substring(0, 1).toUpperCase() : "?" %>
            </div>
            <div style="display: flex; flex-direction: column; line-height: 1.2;">
                <span style="font-weight: bold; font-size: 14px;"><%= currentUsername != null ? currentUsername : "Guest" %></span>
                <span style="font-size: 12px; color: var(--accent-purple);"><%= uStatus %></span>
            </div>
            <a href="LogoutServlet" class="btn btn-sm btn-danger" style="padding: 6px 12px; margin-left: 10px;"><i class="fas fa-sign-out-alt"></i></a>
        </div>
    </div>
</div>

