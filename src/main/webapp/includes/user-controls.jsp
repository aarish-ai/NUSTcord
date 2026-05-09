<%@ page import="com.nustcord.service.ProfileService" %>
<%@ page import="com.nustcord.model.UserStatus" %>
<%
    Integer uId = (Integer) session.getAttribute("userId");
    String currentUsername = (String) session.getAttribute("username");
    String uStatus = "Offline";
    if (uId != null) {
        ProfileService pService = new ProfileService();
        UserStatus statusObj = pService.getStatus(uId);
        if (statusObj != null && statusObj.getStatus() != null) {
            uStatus = statusObj.getStatus();
        }
    }
%>
<div class="user-controls">
    <div class="user-controls-info">
        <span class="user-name"><%= currentUsername != null ? currentUsername : "Guest" %></span>
        <span class="user-status"><%= uStatus %></span>
    </div>
    <div style="display: flex; gap: 8px;">
        <a href="profile.jsp" title="Settings" style="color: var(--text-muted); text-decoration: none;">⚙️</a>
        <a href="LogoutServlet" title="Logout" style="color: var(--danger-red); text-decoration: none;">🚪</a>
    </div>
</div>

