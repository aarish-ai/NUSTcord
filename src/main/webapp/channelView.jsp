<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.Channel" %>
<%@ page import="com.nustcord.service.ChannelService" %>
<%@ page import="com.nustcord.dao.ChannelDAO" %>
<%
    int serverId = Integer.parseInt(request.getParameter("serverId"));
    Connection conn = (Connection) application.getAttribute("DBConnection");
    ChannelService channelService = new ChannelService(new ChannelDAO(conn));
    List<Channel> channels = channelService.getChannels(serverId);
%>
<html>
<head><title>Channels</title></head>
<body>
<h2>Channels</h2>
<ul>
<% for(Channel c : channels) { %>
    <li><%=c.getName()%> (<%=c.getType()%>)</li>
<% } %>
</ul>
<form action="channel" method="post">
    <input type="hidden" name="serverId" value="<%=serverId%>"/>
    <input type="text" name="name" placeholder="Channel Name"/>
    <select name="type">
        <option value="TEXT">Text</option>
        <option value="VOICE">Voice</option>
    </select>
    <button type="submit">Add Channel</button>
</form>
</body>
</html>
