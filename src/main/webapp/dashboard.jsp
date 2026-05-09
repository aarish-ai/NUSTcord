<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Home</title>
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
                    <i class="fas fa-home" style="margin-right: 10px; color: var(--text-muted);"></i> Home
                </div>
                <div class="content-body">
                    <div class="card" style="text-align: center; max-width: 600px; margin: 40px auto;">
                        <i class="fas fa-satellite-dish" style="font-size: 48px; color: var(--accent-purple); margin-bottom: 20px;"></i>
                        <h2 style="margin-bottom: 15px;">Welcome to NUSTcord</h2>
                        <p style="color: var(--text-muted); line-height: 1.6; margin-bottom: 20px;">
                            You're in the right place! Use the sidebar on the left to start a direct message or view your friends. 
                            Want to hang out with a group? Select a server from the top menu or create your own!
                        </p>
                        <div style="display: flex; gap: 15px; justify-content: center;">
                            <a href="friends.jsp" class="btn"><i class="fas fa-user-friends"></i> Find Friends</a>
                            <a href="serverList.jsp" class="btn" style="background-color: var(--bg-hover); color: var(--text-primary);"><i class="fas fa-plus"></i> Add Server</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

