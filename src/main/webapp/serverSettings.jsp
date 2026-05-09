<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.Role" %>
<%@ page import="com.nustcord.service.RoleService" %>
<%@ page import="com.nustcord.dao.RoleDAO" %>
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
    
    RoleService roleService = new RoleService(new RoleDAO());
    List<Role> roles = roleService.getRoles(serverId);
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Server Settings</title>
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
                Roles Management
            </div>
            <div class="content-body">
                <div class="card">
                    <h3 style="margin-bottom: 15px;">Existing Roles</h3>
                    <div style="display: flex; flex-direction: column; gap: 8px;">
                        <% for(Role r : roles) { %>
                            <div class="list-item" style="background-color: var(--bg-base); cursor: default; justify-content: space-between;">
                                <strong><%=r.getName()%></strong>
                                <span style="font-size: 12px; color: var(--text-muted);"><%=r.getPermissions()%></span>
                            </div>
                        <% } %>
                    </div>
                </div>

                <div class="card">
                    <h3 style="margin-bottom: 15px;">Create New Role</h3>
                    <form action="role" method="post">
                        <input type="hidden" name="serverId" value="<%=serverId%>"/>
                        <div class="form-group">
                            <label>Role Name</label>
                            <input type="text" name="name" placeholder="e.g. Moderator" required>
                        </div>
                        <div class="form-group">
                            <label>Permissions (comma separated)</label>
                            <input type="text" name="permissions" placeholder="e.g. KICK_MEMBERS, BAN_MEMBERS" required>
                        </div>
                        <button type="submit" class="btn">Create Role</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

