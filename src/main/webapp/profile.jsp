<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nustcord.model.Profile" %>
<%@ page import="com.nustcord.service.ProfileService" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
    
    ProfileService ps = new ProfileService();
    Profile profile = ps.getProfile(userId);
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Profile</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container container-large">
        <div class="nav">
            <span style="font-weight: bold; color: var(--accent-purple);">NUSTcord</span>
            <div>
                <a href="dashboard.jsp">Dashboard</a> | 
                <a href="profile.jsp">Profile</a> | 
                <a href="friends.jsp">Friends</a> | 
                <a href="LogoutServlet">Logout</a>
            </div>
        </div>

        <h2>User Profile</h2>
        
        <% if (request.getParameter("error") != null) { %>
            <div class="message error"><%= request.getParameter("error") %></div>
        <% } %>
        <% if (request.getParameter("success") != null) { %>
            <div class="message success"><%= request.getParameter("success") %></div>
        <% } %>

        <form action="ProfileServlet" method="POST">
            <div class="form-group">
                <label>Display Name:</label>
                <input type="text" name="displayName" value="<%= profile.getDisplayName() != null ? profile.getDisplayName() : "" %>" required>
            </div>
            <div class="form-group">
                <label>Bio:</label>
                <textarea name="bio" rows="4"><%= profile.getBio() != null ? profile.getBio() : "" %></textarea>
            </div>
            <button type="submit" class="btn">Save Profile</button>
        </form>
    </div>
</body>
</html>
