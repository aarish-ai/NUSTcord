<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Home</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="includes/top-nav.jsp" />
        
        <div class="main-wrapper">
            <jsp:include page="includes/left-sidebar.jsp" />

            <div class="main-content">
                <div class="main-header">
                    Home
                </div>
                <div class="content-body" style="display:flex; justify-content:center; align-items:center; flex-direction:column; color:var(--text-muted); height:100%;">
                    <img src="https://ui-avatars.com/api/?name=NUST&background=5b6eae&color=fff&rounded=true" style="width: 120px; margin-bottom: 20px;">
                    <h2>Welcome to NUSTcord</h2>
                    <p>Select 'Friends' on the left, or join a Server from the Top Menu!</p>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
