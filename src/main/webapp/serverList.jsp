<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.Server" %>
<%@ page import="com.nustcord.service.ServerService" %>
<%@ page import="com.nustcord.dao.ServerDAO" %>
<%@ page import="com.nustcord.dao.UserServerMapDAO" %>
<%
    int userId = (int) session.getAttribute("userId");
    Connection conn = (Connection) application.getAttribute("DBConnection");
    ServerService serverService = new ServerService(new ServerDAO(conn), new UserServerMapDAO(conn));
    List<Server> servers = serverService.getUserServers(userId);
%>
<html>
<head><title>Servers</title></head>
<body>
<h2>Your Servers</h2>
<ul>
<% for(Server s : servers) { %>
    <li><a href="channelView.jsp?serverId=<%=s.getId()%>"><%=s.getName()%></a></li>
<% } %>
</ul>
<form action="server" method="post">
    <input type="text" name="name" placeholder="New Server Name"/>
    <input type="hidden" name="action" value="create"/>
    <button type="submit">Create Server</button>
</form>
</body>
</html>
