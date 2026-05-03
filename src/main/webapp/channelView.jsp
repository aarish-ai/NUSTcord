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
<!DOCTYPE html>
<html>
<head>
    <title>Channels</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
    <div class="container container-large">
        <h2>Channels</h2>
        <ul>
            <% for(Channel c : channels) { %>
                <li>
                    <strong><%= c.getName() %></strong> (<%= c.getType() %>)
                    <!-- Link to chat.jsp for this channel -->
                    <a href="message?channelId=<%= c.getId() %>&serverId=<%= serverId %>">Open Chat</a>
                </li>
            <% } %>
        </ul>

        <h3>Add a New Channel</h3>
        <form action="channel" method="post" style="display: flex; gap: 10px; margin-top: 10px;">
            <input type="hidden" name="serverId" value="<%=serverId%>"/>
            <input type="text" name="name" placeholder="Channel Name" style="flex-grow: 1;"/>
            <select name="type">
                <option value="TEXT">Text</option>
                <option value="VOICE">Voice</option>
            </select>
            <button type="submit" class="btn">Add Channel</button>
        </form>

        <p><a href="serverSettings.jsp?serverId=<%=serverId%>">Back to Server Settings</a></p>
    </div>
</body>
</html>
