<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Register</title>
    <link rel="stylesheet" type="text/css" href="css/style.css?v=2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="standalone-wrapper">
        <div class="standalone-container">
            <h2 style="color: var(--text-primary); font-weight: bold; letter-spacing: 1px;">Create an account</h2>
            
            <% if (request.getParameter("error") != null) { %>
                <div class="message-alert error"><%= request.getParameter("error") %></div>
            <% } %>

            <form action="RegisterServlet" method="POST">
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" required autofocus>
                </div>
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" required>
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" required>
                </div>
                <button type="submit" class="btn btn-block" style="margin-top: 10px;">Continue</button>
            </form>
            <p style="color: var(--text-muted); font-size: 14px; margin-top: 20px;">
                <a href="login.jsp" style="color: var(--accent-purple);">Already have an account?</a>
            </p>
        </div>
    </div>
</body>
</html>

