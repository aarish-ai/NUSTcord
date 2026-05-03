<%@ page import="java.util.List" %>
<%@ page import="com.nustcord.model.Role" %>
<%@ page import="com.nustcord.service.RoleService" %>
<%@ page import="com.nustcord.dao.RoleDAO" %>
<%
    int serverId = Integer.parseInt(request.getParameter("serverId"));
    Connection conn = (Connection) application.getAttribute("DBConnection");
    RoleService roleService = new RoleService(new RoleDAO(conn));
    List<Role> roles = roleService.getRoles(serverId);
%>
<html>
<head><title>Server Settings</title></head>
<body>
<h2>Server Settings</h2>
<ul>
<% for(Role r : roles) { %>
    <li><%=r.getName()%> - <%=r.getPermissions()%></li>
<% } %>
</ul>
<form action="role" method="post">
    <input type="hidden" name="serverId" value="<%=serverId%>"/>
    <input type="text" name="name" placeholder="Role Name"/>
    <input type="text" name="permissions" placeholder="Permissions (comma separated)"/>
    <button type="submit">Add Role</button>
</form>
</body>
</html>
