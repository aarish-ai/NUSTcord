<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.Channel" %>
<%@ page import="com.nustcord.service.ChannelService" %>
<%@ page import="com.nustcord.dao.ChannelDAO" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
    
    String serverIdParam = request.getParameter("serverId");
    if (serverIdParam == null || serverIdParam.isEmpty()) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
    
    int serverId = 0;
    try {
        serverId = Integer.parseInt(serverIdParam);
    } catch (NumberFormatException e) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
    
    ChannelService channelService = new ChannelService(new ChannelDAO());
    List<Channel> channels = channelService.getChannels(serverId);
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Server</title>
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
                Welcome to the Server
            </div>
            <div class="content-body" style="display:flex; justify-content:center; align-items:center; flex-direction:column; color:var(--text-muted); height:100%;">
                <h2 style="color: var(--text-muted);">Welcome!</h2>
                <p>Select a channel on the left to start chatting.</p>
            </div>
        </div>
    </div>
</div>
</body>
</html>

