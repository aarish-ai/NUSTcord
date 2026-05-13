<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) { response.sendRedirect("login.jsp"); return; }
%>
<!DOCTYPE html>
<html>
<head>
    <title>NUSTcord - Discover Servers</title>
    <link rel="stylesheet" type="text/css" href="css/style.css?v=2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        /* ── Server Password Modal ── */
        .modal-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.65);
            z-index: 1000;
            align-items: center;
            justify-content: center;
        }
        .modal-overlay.active { display: flex; }
        .modal-box {
            background: var(--bg-sidebar, #1a1a1f);
            border-radius: 12px;
            padding: 32px 28px 24px;
            width: 100%;
            max-width: 400px;
            box-shadow: 0 8px 32px rgba(0,0,0,0.5);
            border: 1px solid rgba(114,137,218,0.25);
            animation: modalIn 0.18s ease;
        }
        @keyframes modalIn {
            from { opacity: 0; transform: translateY(-12px) scale(0.97); }
            to   { opacity: 1; transform: translateY(0) scale(1); }
        }
        .modal-box h3 {
            margin: 0 0 6px;
            font-size: 1.1rem;
            color: var(--text-primary, #f2f3f5);
        }
        .modal-box p {
            margin: 0 0 18px;
            font-size: 0.88rem;
            color: var(--text-muted, #949ba4);
        }
        .modal-box input[type="password"] {
            width: 100%;
            box-sizing: border-box;
        }
        .modal-actions {
            display: flex;
            gap: 10px;
            margin-top: 18px;
        }
        .modal-actions .btn { flex: 1; }
        .btn-ghost {
            background: transparent;
            border: 1px solid var(--bg-hover, #313139);
            color: var(--text-muted, #949ba4);
        }
        .btn-ghost:hover { background: var(--bg-hover); color: var(--text-primary); transform: none; }

        /* Lock badge on password-protected servers */
        .lock-badge {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            font-size: 0.75rem;
            color: var(--text-muted, #949ba4);
            background: rgba(114,137,218,0.1);
            border: 1px solid rgba(114,137,218,0.2);
            border-radius: 99px;
            padding: 2px 8px;
            margin-left: 8px;
            vertical-align: middle;
        }

        /* Password field in create form */
        .create-form-password-hint {
            font-size: 0.78rem;
            color: var(--text-muted);
            margin-top: -12px;
            margin-bottom: 14px;
        }
    </style>
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
                        <%-- ══ Public server list ══ --%>
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
                                        try { isMember = mapDao.isUserInServer(userId, s.getId()); } catch(Exception e){}
                            %>
                                <div class="list-item" style="padding: 15px; background-color: var(--bg-sidebar); border-radius: 8px; justify-content: space-between; margin-bottom: 10px; cursor: default;">
                                    <div style="display: flex; align-items: center;">
                                        <div style="width: 40px; height: 40px; border-radius: 50%; background-color: var(--bg-hover); display: flex; align-items: center; justify-content: center; font-weight: bold; font-size: 16px; margin-right: 15px; color: var(--text-primary);">
                                            <%= s.getName().substring(0, Math.min(2, s.getName().length())).toUpperCase() %>
                                        </div>
                                        <div>
                                            <div style="font-weight: bold; color: var(--text-primary);">
                                                <%= s.getName() %>
                                                <% if (s.isPasswordProtected()) { %>
                                                    <span class="lock-badge"><i class="fas fa-lock"></i> Password</span>
                                                <% } %>
                                            </div>
                                        </div>
                                    </div>
                                    <% if (isMember) { %>
                                        <a href="channelView.jsp?serverId=<%= s.getId() %>" class="btn btn-sm" style="background-color: var(--bg-hover); color: var(--text-primary); padding: 8px 16px;">View</a>
                                    <% } else if (s.isPasswordProtected()) { %>
                                        <%-- Password-protected: open modal on click --%>
                                        <button type="button"
                                                class="btn btn-sm"
                                                style="padding: 8px 16px;"
                                                onclick="openPasswordModal(<%= s.getId() %>, '<%= s.getName().replace("'", "\\'") %>')">
                                            <i class="fas fa-lock" style="margin-right:4px;"></i>Join
                                        </button>
                                    <% } else { %>
                                        <%-- Open server: direct join form --%>
                                        <form action="joinServer" method="post" style="margin: 0;">
                                            <input type="hidden" name="serverId" value="<%= s.getId() %>">
                                            <button type="submit" class="btn btn-sm" style="padding: 8px 16px;">Join</button>
                                        </form>
                                    <% } %>
                                </div>
                            <%
                                    }
                                } else {
                            %>
                                <p style="color: var(--text-muted); text-align: center;">No servers available. Be the first to create one!</p>
                            <% } %>
                            </div>
                        </div>

                        <%-- ══ Create server panel ══ --%>
                        <div style="flex: 1;">
                            <div class="card" style="background-color: var(--bg-sidebar); padding: 20px; text-align: center;">
                                <h3 style="margin-bottom: 15px;">Create Your Own</h3>
                                <p style="color: var(--text-muted); font-size: 14px; margin-bottom: 20px;">Your server is where you and your friends hang out. Make yours and start talking.</p>
                                <form action="server" method="post" style="text-align: left;">
                                    <div class="form-group">
                                        <label for="serverNameInput" style="display:block; margin-bottom:6px; font-size:0.8rem; color:var(--text-muted); text-transform:uppercase; font-weight:700; letter-spacing:0.5px;">Server Name</label>
                                        <input type="text" id="serverNameInput" name="name" placeholder="E.g. Gaming Lounge" required style="width: 100%;">
                                    </div>
                                    <div class="form-group">
                                        <label for="serverPasswordInput" style="display:block; margin-bottom:6px; font-size:0.8rem; color:var(--text-muted); text-transform:uppercase; font-weight:700; letter-spacing:0.5px;">
                                            <i class="fas fa-lock" style="margin-right:4px;"></i>Server Password <span style="font-weight:400; text-transform:none;">(optional)</span>
                                        </label>
                                        <input type="password" id="serverPasswordInput" name="password" placeholder="Leave blank for an open server" style="width: 100%;">
                                    </div>
                                    <p class="create-form-password-hint">If you set a password, members must enter it to join.</p>
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

    <%-- ══ Password modal (shared, filled dynamically per server) ══ --%>
    <div class="modal-overlay" id="passwordModal" onclick="closeModalOnBackdrop(event)">
        <div class="modal-box">
            <h3><i class="fas fa-lock" style="color:var(--accent-purple); margin-right:8px;"></i>Password Required</h3>
            <p id="modalSubtitle">This server requires a password to join.</p>
            <form action="joinServer" method="post" id="passwordModalForm">
                <input type="hidden" name="serverId" id="modalServerId">
                <div class="form-group" style="margin-bottom:0;">
                    <input type="password"
                           name="serverPassword"
                           id="modalPasswordInput"
                           placeholder="Enter server password"
                           required
                           autocomplete="off">
                </div>
                <div class="modal-actions">
                    <button type="button" class="btn btn-ghost btn-sm" onclick="closePasswordModal()">Cancel</button>
                    <button type="submit" class="btn btn-sm"><i class="fas fa-sign-in-alt" style="margin-right:5px;"></i>Join</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        /**
         * Opens the password prompt modal for a given password-protected server.
         * @param {number} serverId  - The server's database ID
         * @param {string} serverName - The server's display name (shown in subtitle)
         */
        function openPasswordModal(serverId, serverName) {
            document.getElementById('modalServerId').value   = serverId;
            document.getElementById('modalSubtitle').textContent =
                'Enter the password to join "' + serverName + '"';
            document.getElementById('modalPasswordInput').value = '';
            document.getElementById('passwordModal').classList.add('active');
            // Focus the password field for immediate keyboard input
            setTimeout(function() {
                document.getElementById('modalPasswordInput').focus();
            }, 60);
        }

        /** Closes the password modal. */
        function closePasswordModal() {
            document.getElementById('passwordModal').classList.remove('active');
        }

        /** Closes modal when clicking outside the modal box. */
        function closeModalOnBackdrop(event) {
            if (event.target === document.getElementById('passwordModal')) {
                closePasswordModal();
            }
        }

        /** Allow ESC key to dismiss the modal. */
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') closePasswordModal();
        });
    </script>
</body>
</html>
