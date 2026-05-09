<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nustcord.model.Profile" %>
<%@ page import="com.nustcord.service.ProfileService" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
    
    ProfileService ps = new ProfileService();
    Profile profile = ps.getProfile(userId);
    String status = ps.getStatus(userId) != null ? ps.getStatus(userId).getStatus() : "Offline";
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Profile Settings</title>
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
                    <i class="fas fa-user" style="margin-right: 10px; color: var(--text-muted);"></i> My Account
                </div>
                <div class="content-body">
                    <div style="max-width: 600px; margin: 0 auto;">
                        <% if (request.getParameter("error") != null) { %>
                            <div class="message-alert error"><%= request.getParameter("error") %></div>
                        <% } %>
                        <% if (request.getParameter("success") != null) { %>
                            <div class="message-alert success"><%= request.getParameter("success") %></div>
                        <% } %>

                        <div class="card">
                            <h3 style="margin-bottom: 20px; border-bottom: 1px solid var(--bg-hover); padding-bottom: 10px;">Profile Information</h3>
                            <form action="ProfileServlet" method="POST">
                                <div class="form-group">
                                    <label>Display Name</label>
                                    <input type="text" name="displayName" value="<%= profile.getDisplayName() != null ? profile.getDisplayName() : "" %>" required>
                                </div>
                                <div class="form-group">
                                    <label>About Me (Bio)</label>
                                    <textarea name="bio" rows="4"><%= profile.getBio() != null ? profile.getBio() : "" %></textarea>
                                </div>
                                <button type="submit" class="btn">Save Profile</button>
                            </form>
                        </div>

                        <div class="card">
                            <h3 style="margin-bottom: 20px; border-bottom: 1px solid var(--bg-hover); padding-bottom: 10px;">Current Status</h3>
                            <form action="StatusServlet" method="POST" class="flex-row">
                                <select name="status" style="flex: 1;">
                                    <option value="Online" <%= "Online".equals(status) ? "selected" : "" %>>🟢 Online</option>
                                    <option value="Away" <%= "Away".equals(status) ? "selected" : "" %>>🌙 Away</option>
                                    <option value="Busy" <%= "Busy".equals(status) ? "selected" : "" %>>⛔ Do Not Disturb</option>
                                    <option value="Offline" <%= "Offline".equals(status) ? "selected" : "" %>>⚫ Invisible</option>
                                </select>
                                <button type="submit" class="btn btn-sm">Update</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
    </div>
</div>
</body>
</html>

