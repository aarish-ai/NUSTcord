<%@ page import="com.nustcord.model.UserStatus" %>
<%@ page import="com.nustcord.service.ProfileService" %>
<%
    Integer currentUserId = (Integer) session.getAttribute("userId");
    String currentUsername = (String) session.getAttribute("username");
    String uStatus = "Offline";
    if (currentUserId != null) {
        ProfileService pService = new ProfileService();
        UserStatus statusObj = pService.getStatus(currentUserId);
        if (statusObj != null && statusObj.getStatus() != null) {
            uStatus = statusObj.getStatus();
        }
    }
%>
<div class="top-nav">
    <div class="brand">
        NUSTcord
    </div>
    <div class="nav-links">
        <a href="serverList.jsp" class="nav-link">Servers</a>
        <a href="profile.jsp" class="nav-link">Profile</a>
        <a href="profile.jsp" class="nav-link">Settings</a>
        <div style="display: flex; align-items: center; gap: 15px; margin-left: 10px; padding-left: 20px; border-left: 1px solid var(--bg-hover);">
            <div style="display: flex; flex-direction: column; line-height: 1.2;">
                <span style="font-weight: bold; font-size: 14px;"><%= currentUsername != null ? currentUsername : "Guest" %></span>
                <span style="font-size: 12px; color: var(--accent-purple);"><%= uStatus %></span>
            </div>
            <a href="LogoutServlet" class="btn btn-sm btn-danger" style="padding: 6px 12px;">Logout</a>
        </div>
    </div>
</div>
