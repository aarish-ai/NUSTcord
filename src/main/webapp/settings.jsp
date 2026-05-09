<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - App Settings</title>
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
                    <i class="fas fa-cog" style="margin-right: 10px; color: var(--text-muted);"></i> App Settings
                </div>
                <div class="content-body">
                    <div style="max-width: 600px; margin: 0 auto;">
                        <div class="card">
                            <h3 style="margin-bottom: 20px; border-bottom: 1px solid var(--bg-hover); padding-bottom: 10px;">Appearance</h3>
                            <div class="flex-between" style="margin-bottom: 15px;">
                                <div>
                                    <strong>Theme</strong>
                                    <div style="font-size: 12px; color: var(--text-muted);">Select your preferred theme.</div>
                                </div>
                                <select style="width: auto;">
                                    <option>Dark Mode (Default)</option>
                                    <option>Light Mode (Coming Soon)</option>
                                </select>
                            </div>
                        </div>

                        <div class="card">
                            <h3 style="margin-bottom: 20px; border-bottom: 1px solid var(--bg-hover); padding-bottom: 10px;">Account Security</h3>
                            <div class="flex-between" style="margin-bottom: 15px;">
                                <div>
                                    <strong>Password</strong>
                                    <div style="font-size: 12px; color: var(--text-muted);">Update your account password.</div>
                                </div>
                                <button class="btn btn-sm" style="background-color: var(--bg-hover); color: var(--text-primary);">Change Password</button>
                            </div>
                            <div class="flex-between" style="margin-top: 25px; padding-top: 15px; border-top: 1px solid var(--danger-red);">
                                <div>
                                    <strong style="color: var(--danger-red);">Delete Account</strong>
                                    <div style="font-size: 12px; color: var(--text-muted);">Permanently remove your account and all data.</div>
                                </div>
                                <button class="btn btn-sm btn-danger">Delete Account</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
