<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Add Server</title>
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
                    <i class="fas fa-compass" style="margin-right: 10px; color: var(--text-muted);"></i> Discover Servers
                </div>
                <div class="content-body">
                    <% if (request.getParameter("error") != null) { %>
                        <div class="message-alert error"><%= request.getParameter("error") %></div>
                    <% } %>
                    <% if (request.getParameter("success") != null) { %>
                        <div class="message-alert success"><%= request.getParameter("success") %></div>
                    <% } %>
                    <div style="display: flex; gap: 20px; align-items: flex-start;">
                        <div style="flex: 2;">
                            <h3 class="category-title" style="margin-left: 0; margin-bottom: 15px;">Public Servers</h3>
                            <div class="card" style="padding: 15px;">
                            <%
                                com.nustcord.dao.ServerDAO serverDao = new com.nustcord.dao.ServerDAO();
                                com.nustcord.dao.UserServerMapDAO mapDao = new com.nustcord.dao.UserServerMapDAO();
                                java.util.List<com.nustcord.model.Server> allServers = null;
                                try {
                                    allServers = serverDao.getAllServers();
                                } catch (Exception e) {}
                                
                                if (allServers != null && !allServers.isEmpty()) {
                                    for (com.nustcord.model.Server s : allServers) {
                                        boolean isMember = false;
                                        try {
                                            isMember = mapDao.isUserInServer(userId, s.getId());
                                        } catch(Exception e){}
                            %>
                                    <div class="list-item" style="padding: 15px; background-color: var(--bg-sidebar); border-radius: 8px; justify-content: space-between; margin-bottom: 10px; cursor: default;">
                                        <div style="display: flex; align-items: center;">
                                            <div style="width: 40px; height: 40px; border-radius: 50%; background-color: var(--bg-hover); display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 16px; margin-right: 15px; color: var(--text-primary);">
                                                <%= s.getName().substring(0, Math.min(2, s.getName().length())).toUpperCase() %>
                                            </div>
                                            <div>
                                                <div style="font-weight: bold; color: var(--text-primary);"><%= s.getName() %></div>
                                            </div>
                                        </div>
                                        <% if (isMember) { %>
                                            <a href="channelView.jsp?serverId=<%= s.getId() %>" class="btn btn-sm" style="background-color: var(--bg-hover); color: var(--text-primary); padding: 8px 16px;">View</a>
                                        <% } else { %>
                                            <form action="joinServer" method="post" style="margin: 0;">
                                                <input type="hidden" name="serverId" value="<%= s.getId() %>">
                                                <button type="submit" class="btn btn-sm" style="padding: 8px 16px;">Join</button>
                                            </form>
                                        <% } %>
                                    </div>
                            <%      }
                                } else { %>
                                    <p style="color: var(--text-muted); text-align: center;">No servers available. Be the first to create one!</p>
                            <%  } %>
                            </div>
                        </div>

                        <div style="flex: 1;">
                            <div class="card" style="background-color: var(--bg-sidebar); padding: 20px; text-align: center;">
                                <h3 style="margin-bottom: 15px;">Create Your Own</h3>
                                <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 20px;">Your server is where you and your friends hang out. Make yours and start talking.</p>
                                <form action="server" method="post" style="text-align: left;">
                                    <div class="form-group">
                                        <input type="text" name="name" placeholder="E.g. Gaming Lounge" required style="width: 100%;">
                                    </div>
                                    <input type="hidden" name="action" value="create"/>
                                    <button type="submit" class="btn btn-block" style="margin-top: 10px;">Create Server</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>

