<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nustcord.dao.AdminDAO.AdminUserRow" %>
<%@ page import="java.util.List" %>
<%--
  adminDashboard.jsp
  Purpose: Admin-only view that displays a sortable, searchable table of all
           registered users with aggregated statistics.
  Access:  Only reachable via AdminServlet (/admin) which enforces the admin
           session check. Direct URL access will redirect to login.
--%>
<%
    /* Secondary session guard: even if someone navigates here directly,
       we check for the username attribute and verify it is "admin". */
    String adminUser = (String) session.getAttribute("username");
    if (adminUser == null || !adminUser.equals("admin")) {
        response.sendRedirect("login.jsp?error=Unauthorized");
        return;
    }

    /* Retrieve the user list set by AdminServlet; may be null on DB error. */
    List<AdminUserRow> users = (List<AdminUserRow>) request.getAttribute("users");
    String dbError = (String) request.getAttribute("dbError");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NUSTcord – Admin Dashboard</title>
    <meta name="description" content="NUSTcord admin panel – manage and inspect all registered users.">
    <link rel="stylesheet" href="css/style.css?v=2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        /* ─── Admin Dashboard Scoped Styles ─── */

        /* Full-height dark background matching the app theme */
        .admin-wrapper {
            min-height: 100vh;
            background: var(--bg-primary, #1a1b2e);
            color: var(--text-primary, #e0e0ff);
            font-family: 'Segoe UI', sans-serif;
        }

        /* Top admin header bar */
        .admin-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 18px 32px;
            background: linear-gradient(90deg, #5865f2, #7c3aed);
            box-shadow: 0 2px 12px rgba(88,101,242,0.4);
        }
        .admin-header h1 {
            font-size: 1.4rem;
            font-weight: 700;
            margin: 0;
            color: #fff;
            letter-spacing: 1px;
        }
        .admin-header .back-btn {
            color: rgba(255,255,255,0.85);
            text-decoration: none;
            font-size: 0.9rem;
            border: 1px solid rgba(255,255,255,0.3);
            padding: 6px 16px;
            border-radius: 6px;
            transition: background 0.2s;
        }
        .admin-header .back-btn:hover {
            background: rgba(255,255,255,0.15);
        }

        /* Main content area with comfortable padding */
        .admin-content {
            max-width: 1400px;
            margin: 0 auto;
            padding: 32px 24px;
        }

        /* Stats summary row at the top */
        .stats-row {
            display: flex;
            gap: 20px;
            margin-bottom: 28px;
            flex-wrap: wrap;
        }
        .stat-card {
            flex: 1;
            min-width: 160px;
            background: rgba(255,255,255,0.05);
            border: 1px solid rgba(255,255,255,0.08);
            border-radius: 12px;
            padding: 20px 24px;
            text-align: center;
            backdrop-filter: blur(8px);
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .stat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 24px rgba(88,101,242,0.2);
        }
        .stat-card .stat-number {
            font-size: 2rem;
            font-weight: 800;
            color: #7c3aed;
        }
        .stat-card .stat-label {
            font-size: 0.8rem;
            color: var(--text-muted, #8e9297);
            margin-top: 4px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        /* Search and filter toolbar */
        .toolbar {
            display: flex;
            align-items: center;
            gap: 14px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }
        .search-box {
            flex: 1;
            min-width: 240px;
            display: flex;
            align-items: center;
            background: rgba(255,255,255,0.06);
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 8px;
            padding: 0 14px;
            gap: 10px;
        }
        .search-box i {
            color: var(--text-muted, #8e9297);
        }
        .search-box input {
            background: transparent;
            border: none;
            outline: none;
            color: var(--text-primary, #e0e0ff);
            font-size: 0.95rem;
            padding: 10px 0;
            width: 100%;
        }
        .search-box input::placeholder {
            color: var(--text-muted, #8e9297);
        }

        /* Status filter dropdown */
        .filter-select {
            background: rgba(255,255,255,0.06);
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 8px;
            color: var(--text-primary, #e0e0ff);
            padding: 10px 14px;
            font-size: 0.9rem;
            cursor: pointer;
            outline: none;
        }
        .filter-select option {
            background: #2d2f45;
        }

        /* User count badge */
        .user-count-badge {
            font-size: 0.85rem;
            color: var(--text-muted, #8e9297);
            white-space: nowrap;
        }

        /* Table container with horizontal scroll for mobile */
        .table-container {
            background: rgba(255,255,255,0.03);
            border: 1px solid rgba(255,255,255,0.07);
            border-radius: 12px;
            overflow-x: auto;
            box-shadow: 0 4px 24px rgba(0,0,0,0.2);
        }

        /* Main data table */
        #userTable {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
        }
        #userTable thead {
            background: linear-gradient(135deg, rgba(88,101,242,0.3), rgba(124,58,237,0.3));
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        #userTable thead th {
            padding: 14px 16px;
            text-align: left;
            font-weight: 600;
            color: var(--text-primary, #e0e0ff);
            white-space: nowrap;
            cursor: pointer;
            user-select: none;
            transition: background 0.15s;
        }
        #userTable thead th:hover {
            background: rgba(88,101,242,0.2);
        }
        /* Sort arrow indicator */
        #userTable thead th::after {
            content: ' ↕';
            opacity: 0.35;
            font-size: 0.75rem;
        }
        #userTable thead th.sorted-asc::after  { content: ' ↑'; opacity: 1; }
        #userTable thead th.sorted-desc::after { content: ' ↓'; opacity: 1; }

        /* Alternating row colors for readability */
        #userTable tbody tr {
            border-bottom: 1px solid rgba(255,255,255,0.04);
            transition: background 0.15s;
        }
        #userTable tbody tr:nth-child(even) {
            background: rgba(255,255,255,0.025);
        }
        #userTable tbody tr:hover {
            background: rgba(88,101,242,0.1);
        }

        #userTable td {
            padding: 12px 16px;
            vertical-align: middle;
            white-space: nowrap;
        }

        /* User ID badge */
        .user-id-badge {
            background: rgba(88,101,242,0.2);
            color: #818cf8;
            font-weight: 700;
            font-size: 0.8rem;
            padding: 3px 9px;
            border-radius: 99px;
        }

        /* Status pill badge */
        .status-pill {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            padding: 4px 10px;
            border-radius: 99px;
            font-size: 0.8rem;
            font-weight: 600;
        }
        .status-online  { background: rgba(87,242,135,0.15); color: #57f287; }
        .status-offline { background: rgba(142,146,151,0.15); color: #8e9297; }
        .status-busy    { background: rgba(237,66,69,0.15);   color: #ed4245; }
        .status-away    { background: rgba(250,168,26,0.15);  color: #faa81a; }
        .status-dot     { width: 7px; height: 7px; border-radius: 50%; background: currentColor; }

        /* Numeric count cells */
        .count-cell {
            font-weight: 700;
            color: #a5b4fc;
        }

        /* Timestamp cells – slightly muted */
        .ts-cell { color: var(--text-muted, #8e9297); font-size: 0.82rem; }

        /* Empty state message */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: var(--text-muted, #8e9297);
        }
        .empty-state i {
            font-size: 3rem;
            margin-bottom: 16px;
            color: rgba(88,101,242,0.5);
        }

        /* Error alert */
        .db-error-banner {
            background: rgba(237,66,69,0.15);
            border: 1px solid rgba(237,66,69,0.3);
            border-radius: 10px;
            padding: 18px 24px;
            margin-bottom: 24px;
            color: #ed4245;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        /* Hidden row class (used by JS search/filter) */
        .row-hidden { display: none; }

        /* Responsive: stack stats on small screens */
        @media (max-width: 640px) {
            .admin-header { padding: 14px 18px; }
            .admin-content { padding: 20px 14px; }
            .stats-row { gap: 12px; }
            .stat-card { min-width: 130px; padding: 14px 16px; }
        }
    </style>
</head>
<body>
<div class="admin-wrapper">

    <%-- ══ Top header ══ --%>
    <header class="admin-header">
        <h1><i class="fas fa-shield-alt" style="margin-right:10px;"></i>Admin Dashboard</h1>
        <a href="dashboard.jsp" class="back-btn" id="backToDashboard">
            <i class="fas fa-arrow-left" style="margin-right:6px;"></i>Back to App
        </a>
    </header>

    <div class="admin-content">

        <%-- ══ Database error banner (shown only when DB query failed) ══ --%>
        <% if (dbError != null) { %>
        <div class="db-error-banner" id="dbErrorBanner">
            <i class="fas fa-exclamation-triangle fa-lg"></i>
            <div>
                <strong>Database Error</strong><br>
                <span style="font-size:0.9rem;"><%= dbError %></span>
            </div>
        </div>
        <% } %>

        <%-- ══ Summary stat cards ══ --%>
        <%
            /* Calculate summary stats for the top cards */
            int totalUsers   = (users != null) ? users.size() : 0;
            int onlineUsers  = 0;
            int totalFriends = 0;
            int totalServers = 0;
            if (users != null) {
                for (AdminUserRow u : users) {
                    if ("Online".equals(u.status)) onlineUsers++;
                    totalFriends += u.friendCount;
                    totalServers += u.serverCount;
                }
            }
        %>
        <div class="stats-row" id="statCards">
            <div class="stat-card">
                <div class="stat-number" id="statTotalUsers"><%= totalUsers %></div>
                <div class="stat-label">Total Users</div>
            </div>
            <div class="stat-card">
                <div class="stat-number" id="statOnlineUsers" style="color:#57f287;"><%= onlineUsers %></div>
                <div class="stat-label">Online Now</div>
            </div>
            <div class="stat-card">
                <div class="stat-number" id="statTotalFriends"><%= totalFriends %></div>
                <div class="stat-label">Friendships</div>
            </div>
            <div class="stat-card">
                <div class="stat-number" id="statTotalServers"><%= totalServers %></div>
                <div class="stat-label">Server Memberships</div>
            </div>
        </div>

        <%-- ══ Search & filter toolbar ══ --%>
        <div class="toolbar" id="toolbarRow">
            <div class="search-box">
                <i class="fas fa-search"></i>
                <input type="text" id="searchInput" placeholder="Search by username, email or display name…"
                       oninput="filterTable()" autofocus>
            </div>
            <select class="filter-select" id="statusFilter" onchange="filterTable()">
                <option value="">All Statuses</option>
                <option value="Online">🟢 Online</option>
                <option value="Offline">⚫ Offline</option>
                <option value="Busy">🔴 Busy</option>
                <option value="Away">🟡 Away</option>
            </select>
            <span class="user-count-badge" id="visibleCount">
                Showing <strong id="visibleNum"><%= totalUsers %></strong> of <strong><%= totalUsers %></strong> users
            </span>
        </div>

        <%-- ══ User data table ══ --%>
        <div class="table-container" id="tableContainer">
            <table id="userTable">
                <thead>
                    <tr>
                        <%-- Column headers are clickable for client-side sorting --%>
                        <th onclick="sortTable(0)" id="thId">ID</th>
                        <th onclick="sortTable(1)" id="thUsername">Username</th>
                        <th onclick="sortTable(2)" id="thEmail">Email</th>
                        <th onclick="sortTable(3)" id="thDisplayName">Display Name</th>
                        <th onclick="sortTable(4)" id="thRegistered">Registered</th>
                        <th onclick="sortTable(5)" id="thStatus">Status</th>
                        <th onclick="sortTable(6)" id="thFriends">Friends</th>
                        <th onclick="sortTable(7)" id="thServers">Servers</th>
                        <th onclick="sortTable(8)" id="thLastLogin">Last Login</th>
                    </tr>
                </thead>
                <tbody id="userTableBody">
                <%
                    /* Render one <tr> per user, or show the empty state if list is empty */
                    if (users == null || users.isEmpty()) {
                %>
                    <tr id="emptyRow">
                        <td colspan="9">
                            <div class="empty-state">
                                <i class="fas fa-users-slash"></i>
                                <p style="font-size:1.1rem; font-weight:600; margin:0 0 8px;">No Users Found</p>
                                <p style="margin:0;">The database returned no user records.</p>
                            </div>
                        </td>
                    </tr>
                <%
                    } else {
                        /* Iterate over every AdminUserRow from the DAO */
                        for (AdminUserRow u : users) {
                            /* Determine status CSS class for the pill badge */
                            String statusClass;
                            switch (u.status) {
                                case "Online":  statusClass = "status-online";  break;
                                case "Busy":    statusClass = "status-busy";    break;
                                case "Away":    statusClass = "status-away";    break;
                                default:        statusClass = "status-offline"; break;
                            }

                            /* Format timestamps cleanly; show "Never" if null */
                            String regDate   = (u.registeredAt != null) ? u.registeredAt.toString().substring(0, 19) : "Unknown";
                            String lastLogin = (u.lastLogin    != null) ? u.lastLogin.toString().substring(0, 19)    : "Never";
                %>
                    <tr id="userRow<%= u.userId %>"
                        data-username="<%= u.username.toLowerCase() %>"
                        data-email="<%= u.email.toLowerCase() %>"
                        data-displayname="<%= u.displayName.toLowerCase() %>"
                        data-status="<%= u.status %>">

                        <td><span class="user-id-badge">#<%= u.userId %></span></td>
                        <td><strong><%= u.username %></strong></td>
                        <td><span style="color:var(--text-muted,#8e9297);"><%= u.email %></span></td>
                        <td><%= u.displayName %></td>
                        <td class="ts-cell"><%= regDate %></td>
                        <td>
                            <span class="status-pill <%= statusClass %>">
                                <span class="status-dot"></span>
                                <%= u.status %>
                            </span>
                        </td>
                        <td class="count-cell"><%= u.friendCount %></td>
                        <td class="count-cell"><%= u.serverCount %></td>
                        <td class="ts-cell"><%= lastLogin %></td>
                    </tr>
                <%
                        } /* end for each user */
                    } /* end if/else */
                %>
                </tbody>
            </table>
        </div><%-- end table-container --%>

    </div><%-- end admin-content --%>
</div><%-- end admin-wrapper --%>

<script>
/**
 * Client-side search and filter logic.
 * Reads the search input and status dropdown, then hides rows that don't match.
 * Also updates the "Showing X of Y users" counter.
 */
function filterTable() {
    // Get current filter values (lowercased for case-insensitive matching)
    var query  = document.getElementById('searchInput').value.toLowerCase().trim();
    var status = document.getElementById('statusFilter').value;
    var rows   = document.querySelectorAll('#userTableBody tr[data-username]');
    var visible = 0;

    rows.forEach(function(row) {
        // Check if the row matches the text search across three columns
        var matchesText = !query
            || row.dataset.username.includes(query)
            || row.dataset.email.includes(query)
            || row.dataset.displayname.includes(query);

        // Check if the row matches the selected status filter
        var matchesStatus = !status || row.dataset.status === status;

        if (matchesText && matchesStatus) {
            row.classList.remove('row-hidden');
            visible++;
        } else {
            row.classList.add('row-hidden');
        }
    });

    // Update the visible count display
    document.getElementById('visibleNum').textContent = visible;
}

/**
 * Client-side column sorting.
 * Toggles between ascending and descending on repeated clicks.
 * Uses a simple string comparison; numeric columns are compared as numbers.
 *
 * @param {number} colIndex - Zero-based index of the column header clicked.
 */
var sortState = {}; // Tracks sort direction per column
function sortTable(colIndex) {
    var table   = document.getElementById('userTable');
    var headers = table.querySelectorAll('thead th');
    var tbody   = table.querySelector('tbody');
    var rows    = Array.from(tbody.querySelectorAll('tr[data-username]'));

    // Determine sort direction: toggle if same column clicked again
    var asc = sortState[colIndex] !== true;
    sortState = {};            // Reset all columns
    sortState[colIndex] = asc; // Set this column's state

    // Update header arrow indicators
    headers.forEach(function(h, i) {
        h.classList.remove('sorted-asc', 'sorted-desc');
        if (i === colIndex) h.classList.add(asc ? 'sorted-asc' : 'sorted-desc');
    });

    // Numeric columns: 0=ID, 6=Friends, 7=Servers
    var isNumeric = [0, 6, 7].includes(colIndex);

    rows.sort(function(a, b) {
        var aText = a.cells[colIndex] ? a.cells[colIndex].textContent.replace(/[#\s]/g,'').trim() : '';
        var bText = b.cells[colIndex] ? b.cells[colIndex].textContent.replace(/[#\s]/g,'').trim() : '';

        if (isNumeric) {
            // Parse as integers for correct numeric ordering
            return asc ? parseInt(aText) - parseInt(bText)
                       : parseInt(bText) - parseInt(aText);
        }
        // Lexicographic comparison for string columns
        return asc ? aText.localeCompare(bText) : bText.localeCompare(aText);
    });

    // Re-append rows in sorted order
    rows.forEach(function(r) { tbody.appendChild(r); });
}
</script>
</body>
</html>
