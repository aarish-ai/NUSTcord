<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Login</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="standalone-wrapper">
        <div class="standalone-container">
            <h2 style="color: var(--text-primary); font-weight: bold; letter-spacing: 1px;">Welcome back!</h2>
            <p style="text-align: center; color: var(--text-muted); margin-bottom: 24px; margin-top: -15px;">We're so excited to see you again!</p>
            
            <% if (request.getParameter("error") != null) { %>
                <div class="message-alert error"><%= request.getParameter("error") %></div>
            <% } %>
            <% if (request.getParameter("success") != null) { %>
                <div class="message-alert success"><%= request.getParameter("success") %></div>
            <% } %>

            <form action="LoginServlet" method="POST">
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" required autofocus>
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-block" style="margin-top: 10px;">Log In</button>
            </form>
            <p style="color: var(--text-muted); font-size: 14px; margin-top: 20px;">
                Need an account? <a href="register.jsp" style="color: var(--accent-purple);">Register</a>
            </p>
        </div>
    </div>
</body>
</html>
