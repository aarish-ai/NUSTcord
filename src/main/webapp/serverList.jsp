<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Add Server</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="includes/top-nav.jsp" />
        
        <div class="main-wrapper">
            <jsp:include page="includes/left-sidebar.jsp" />

            <div class="main-content">
            <div class="main-header">
                Create a Server
            </div>
            <div class="content-body" style="display: flex; justify-content: center; padding-top: 50px;">
                <div class="card" style="width: 100%; max-width: 450px; background-color: var(--bg-base); text-align: center;">
                    <h2 style="margin-bottom: 10px;">Create Your Server</h2>
                    <p style="color: var(--text-muted); margin-bottom: 24px; font-size: 14px;">Your server is where you and your friends hang out. Make yours and start talking.</p>
                    
                    <form action="server" method="post" style="text-align: left;">
                        <div class="form-group">
                            <label>Server Name</label>
                            <input type="text" name="name" placeholder="E.g. Gaming Lounge" required autofocus>
                        </div>
                        <input type="hidden" name="action" value="create"/>
                        <button type="submit" class="btn btn-block" style="margin-top: 20px;">Create</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
