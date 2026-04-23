<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nustcord.model.UserStatus" %>
<%@ page import="com.nustcord.service.ProfileService" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
    
    ProfileService ps = new ProfileService();
    UserStatus us = ps.getStatus(userId);
    String currentStatus = (us != null && us.getStatus() != null) ? us.getStatus() : "Offline";
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Dashboard</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container container-large">
        <div class="nav">
            <span style="font-weight: bold; color: var(--accent-purple);">Welcome, <%= session.getAttribute("username") %></span>
            <div>
                <a href="dashboard.jsp">Dashboard</a> | 
                <a href="profile.jsp">Profile</a> | 
                <a href="friends.jsp">Friends</a> | 
                <a href="LogoutServlet">Logout</a>
            </div>
        </div>

        <h2>Dashboard</h2>
        
        <% if (request.getParameter("error") != null) { %>
            <div class="message error"><%= request.getParameter("error") %></div>
        <% } %>
        <% if (request.getParameter("success") != null) { %>
            <div class="message success"><%= request.getParameter("success") %></div>
        <% } %>

        <div style="background: #2a2a30; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
            <h3>Your Status</h3>
            <p>Current Status: <strong><%= currentStatus %></strong></p>
            <form action="StatusServlet" method="POST" style="display: flex; gap: 10px;">
                <select name="status" style="width: auto; flex-grow: 1;">
                    <option value="Online" <%= "Online".equals(currentStatus) ? "selected" : "" %>>Online</option>
                    <option value="Away" <%= "Away".equals(currentStatus) ? "selected" : "" %>>Away</option>
                    <option value="Busy" <%= "Busy".equals(currentStatus) ? "selected" : "" %>>Busy</option>
                    <option value="Offline" <%= "Offline".equals(currentStatus) ? "selected" : "" %>>Offline</option>
                </select>
                <button type="submit" class="btn" style="width: auto; margin-top: 0;">Update Status</button>
            </form>
        </div>
        
        <p>This is Phase A of NUSTcord. Use the navigation to explore your profile and friends list.</p>
    </div>
</body>
</html>
