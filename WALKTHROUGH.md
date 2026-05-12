# NUSTcord — Complete Code Walkthrough

> **Target audience:** Intermediate Java developers familiar with basic OOP, SQL, and HTML/CSS.

---

## 1. Project Overview

NUSTcord is a **Discord-inspired group chat and social platform** built as a Java web application. Users can register, log in, join or create servers, chat in text channels, send direct messages, and manage a friends list. An admin user can inspect all registered accounts via a dedicated dashboard.

**Key features:**
- User authentication with BCrypt password hashing
- Friend request system (send, accept, reject)
- Multi-server discovery, creation, and joining
- Per-server text channels with real-time-style messaging
- Direct messaging (DMs) between friends
- Admin panel displaying all user statistics

**Technology stack:**
| Layer | Technology |
|---|---|
| Backend language | Java 11 |
| Web framework | Java Servlets + JSP |
| Build tool | Apache Maven |
| Database | H2 (embedded, MySQL-compatible mode) |
| Password hashing | jBCrypt |
| Server | Apache Tomcat 9+ |
| Frontend | HTML, CSS (custom), Vanilla JS |

**Architecture pattern:** MVC (Model-View-Controller)
- **Model** → `com.nustcord.model` (POJOs like `User`, `Server`, `Message`)
- **View** → JSP files in `src/main/webapp/`
- **Controller** → Servlet classes in `com.nustcord.servlet`
- **DAO layer** → `com.nustcord.dao` sits between Controller and the database

---

## 2. Project Structure

```
NUSTcord/
├── pom.xml                          ← Maven build config, dependencies
├── schema.sql                       ← SQL DDL for all database tables
├── src/main/
│   ├── java/com/nustcord/
│   │   ├── dao/                     ← Database Access Objects (SQL queries)
│   │   │   ├── AdminDAO.java        ← Admin user stats aggregation
│   │   │   ├── UserDAO.java         ← User account CRUD
│   │   │   ├── MessageDAO.java      ← Chat message persistence
│   │   │   ├── ServerDAO.java       ← Server management
│   │   │   ├── ChannelDAO.java      ← Channel management
│   │   │   ├── FriendsDAO.java      ← Accepted friendship pairs
│   │   │   ├── FriendRequestDAO.java← Friend request workflow
│   │   │   ├── DirectMessageDAO.java← DM conversation persistence
│   │   │   ├── ProfileDAO.java      ← User profile display names
│   │   │   ├── StatusDAO.java       ← Online/Offline status
│   │   │   ├── RoleDAO.java         ← Server roles (Admin, Member)
│   │   │   └── UserServerMapDAO.java← Many-to-many: users <-> servers
│   │   ├── model/                   ← Plain Java data objects (POJOs)
│   │   │   ├── User.java
│   │   │   ├── Server.java
│   │   │   ├── Channel.java
│   │   │   ├── Message.java
│   │   │   ├── DirectMessage.java
│   │   │   ├── Friend.java
│   │   │   ├── FriendRequest.java
│   │   │   ├── Profile.java
│   │   │   ├── Role.java
│   │   │   └── UserStatus.java
│   │   ├── servlet/                 ← HTTP request controllers
│   │   │   ├── AdminServlet.java    ← /admin  (admin dashboard)
│   │   │   ├── LoginServlet.java    ← /LoginServlet
│   │   │   ├── RegisterServlet.java ← /RegisterServlet
│   │   │   ├── ServerServlet.java   ← /server
│   │   │   ├── ChannelServlet.java  ← /channel
│   │   │   ├── MessageServlet.java  ← /message
│   │   │   ├── FriendServlet.java   ← /FriendServlet
│   │   │   ├── DirectMessageServlet.java ← /directMessage
│   │   │   ├── JoinServerServlet.java    ← /joinServer
│   │   │   ├── ProfileServlet.java       ← /ProfileServlet
│   │   │   ├── LogoutServlet.java        ← /LogoutServlet
│   │   │   ├── StatusServlet.java        ← /StatusServlet
│   │   │   └── RoleServlet.java          ← /RoleServlet
│   │   ├── service/                 ← Business logic layer
│   │   │   ├── AuthService.java
│   │   │   ├── ServerService.java
│   │   │   ├── MessageService.java
│   │   │   ├── ChannelService.java
│   │   │   ├── FriendService.java
│   │   │   ├── DirectMessageService.java
│   │   │   └── ProfileService.java
│   │   ├── filter/
│   │   │   └── AuthFilter.java      ← Guards protected JSP pages
│   │   ├── exception/
│   │   │   └── AuthException.java   ← Custom exception for auth errors
│   │   └── util/
│   │       └── DBConnection.java    ← JDBC connection factory
│   └── webapp/                      ← All browser-facing resources
│       ├── css/style.css            ← Unified dark-theme stylesheet
│       ├── js/                      ← Client-side JavaScript
│       ├── includes/                ← Reusable JSP fragments (nav, sidebar)
│       ├── login.jsp, register.jsp  ← Auth pages
│       ├── dashboard.jsp            ← Main landing after login
│       ├── chat.jsp                 ← Server channel chat view
│       ├── friends.jsp              ← Friends management
│       ├── directMessage.jsp        ← DM conversation view
│       ├── serverList.jsp           ← Browse and create servers
│       ├── settings.jsp             ← User settings
│       └── adminDashboard.jsp       ← Admin-only user table
```

---

## 3. Database Schema

The H2 database (in MySQL-compatibility mode) is structured around these core tables:

| Table | Purpose | Key Relationships |
|---|---|---|
| `users` | All registered accounts | Root table; everything references it |
| `profiles` | Optional display name and bio | 1-to-1 with `users` |
| `user_status` | Online/Offline/Busy/Away status | 1-to-1 with `users` |
| `servers` | Chat servers (communities) | `owner_id` references `users.id` |
| `user_server_map` | Many-to-many: users to servers | Also tracks role per server |
| `channels` | Text/voice channels inside a server | `server_id` references `servers.id` |
| `messages` | All messages sent in channels | `channel_id` + `sender_id` |
| `friends` | Accepted friendship pairs | `user_id1 < user_id2` (ordered) |
| `friend_requests` | Pending/accepted/rejected requests | `sender_id` + `receiver_id` |
| `direct_messages` | DM conversations | `sender_id` + `receiver_id` |
| `roles` | Per-server roles (Admin, Member) | `server_id` references `servers.id` |

---

## 4. Request Flow Explanation

### Flow A: User Login

```
1. User submits login.jsp form (POST /LoginServlet)
   --> Browser sends username + password as form-encoded body

2. LoginServlet.doPost() executes:
   |-- Reads username + password parameters
   |-- Calls authService.login(username, password)
   |     |--> AuthService calls UserDAO.getUserByUsername()
   |           |--> SQL: SELECT * FROM users WHERE username = ?
   |     |--> BCrypt.checkpw() verifies the stored hash
   |     |--> Throws AuthException if credentials don't match
   |-- On success: creates HttpSession, stores userId + username
   |-- Calls profileService.updateStatus(userId, "Online")
   +--> Redirects admin to /admin; regular users to loading.jsp

3. loading.jsp: JavaScript timer -> redirects to dashboard.jsp after 2 s

4. dashboard.jsp:
   +--> AuthFilter confirms session is valid; page renders normally
```

### Flow B: Sending a Channel Message

```
1. User types text in chat.jsp and presses Enter
   --> POST /message?channelId=X&serverId=Y

2. MessageServlet.doPost() executes:
   |-- Validates session (userId in session)
   |-- Validates channelId (not null, must be integer)
   |-- Validates content (not blank)
   |-- Builds a Message model object
   +--> Calls messageService.sendMessage(msg)
         |--> MessageDAO.saveMessage():
               INSERT INTO messages (channel_id, sender_id, content) VALUES (?, ?, ?)

3. On success: redirect to GET /message?channelId=X&serverId=Y

4. MessageServlet.doGet():
   |-- Calls messageService.getMessages(channelId)
   |     |--> MessageDAO.getMessagesByChannel():
   |           SELECT * FROM messages WHERE channel_id = ? ORDER BY created_at ASC
   |-- Sets messages list as request attribute "messages"
   +--> Forwards to chat.jsp

5. chat.jsp renders all messages in chronological order:
   +--> For each message, calls ProfileService.getProfile() for author name
```

### Flow C: Sending a Friend Request

```
1. User submits friends.jsp form (POST /FriendServlet?action=send&receiverUsername=alice)

2. FriendServlet.doPost():
   |-- Verifies session
   |-- Reads action="send", receiverUsername="alice"
   |-- UserDAO.getUserByUsername("alice") --> User object or null
   |-- If null: throws Exception("User 'alice' not found")
   +--> friendService.sendFriendRequest(myId, aliceId)
         |--> FriendRequestDAO.createRequest():
               INSERT INTO friend_requests ... VALUES (?, ?, 'PENDING')

3. Alice opens friends.jsp:
   |--> FriendRequestDAO.getPendingRequestsByReceiver(aliceId)
   +--> Shows "Bob wants to be your friend" with Accept/Reject buttons

4. Alice clicks Accept -> POST /FriendServlet?action=accept&requestId=Z
   |-- friendService.acceptRequest(Z, aliceId)
   |     |-- updateRequestStatus(Z, "ACCEPTED")
   |     +--> FriendsDAO.addFriend(aliceId, bobId)
   |           INSERT IGNORE INTO friends (user_id1, user_id2) VALUES (min, max)
   +--> Redirect to friends.jsp?success=Request accepted.
```

### Flow D: Creating a Server

```
1. User fills in serverList.jsp create form (POST /server?action=create&name=MyServer)

2. ServerServlet.doPost():
   |-- Validates session
   |-- Reads name = "MyServer"
   +--> serverService.createServer(server, userId)
         |-- ServerDAO.createServer()  --> INSERT into servers, get generated ID
         |-- RoleDAO.createRole("Admin", "ALL") for this server
         |-- UserServerMapDAO.joinServer(userId, serverId, adminRoleId)
         +--> ChannelDAO.createChannel("general", TEXT)

3. Redirect to serverList.jsp where the new server is now visible
```

---

## 5. Key Java Files Explained

### `LoginServlet.java`
**Purpose:** Entry point for all authentication.

- **doPost():** Reads `username` and `password`, delegates to `AuthService.login()`, creates the HTTP session on success. Admin users are sent to `/admin`; regular users go to `loading.jsp`.
- **Error path:** `AuthException` is caught and the user is redirected to `login.jsp?error=...` — no HTTP 500 is ever returned.
- **Connects to:** `AuthService` → `UserDAO` → database, `ProfileService.updateStatus()`.

### `AdminServlet.java`
**Purpose:** Sole gateway to the admin dashboard with two-layer access control.

- **doGet():** Checks `session.getAttribute("username").equals("admin")`. If not, redirects to login. If yes, calls `AdminDAO.getAllUsersWithStats()` and forwards to `adminDashboard.jsp`.
- **Error handling:** `SQLException` is caught and stored as `"dbError"` request attribute — the JSP renders a friendly error banner instead of crashing.
- **Connects to:** `AdminDAO`, `adminDashboard.jsp`.

### `UserDAO.java`
**Purpose:** All database operations for the `users` table.

- **`registerUser(User)`:** Inserts a new row; password must already be BCrypt-hashed.
- **`getUserByUsername(String)`:** Used by `AuthService` during login for hash comparison.
- **`getUserById(int)`:** Expands a session `userId` integer into a full `User` object.
- **SQL injection prevention:** All queries use `PreparedStatement` with `?` placeholders.

### `MessageServlet.java`
**Purpose:** Dual-role controller for channel chat.

- **doPost():** Saves a new message; redirects back to doGet to avoid double-submission.
- **doGet():** Loads messages for a channel and forwards to `chat.jsp` via a request attribute.
- **Connects to:** `MessageService` → `MessageDAO`.

### `chat.jsp`
**Purpose:** Renders the full chat UI for a server text channel.

- **Java section:** Session guard, parameter parsing, channel lookup, message retrieval from request attributes.
- **HTML section:** Iterates the `messages` list; calls `ProfileService.getProfile()` per message for the author's display name.
- **Form:** Posts to `/message` servlet with `channelId` and `serverId` hidden fields.

### `AdminDAO.java`
**Purpose:** Aggregates data across multiple tables for the admin dashboard.

- **`getAllUsersWithStats()`:** A single SQL SELECT with LEFT JOINs to `profiles` and `user_status`, plus correlated subqueries for `friend_count` and `server_count`. Returns `List<AdminUserRow>`.
- Inner class `AdminUserRow` is a simple data holder (no logic) designed exclusively for the admin view.

### `AuthFilter.java`
**Purpose:** Login-wall protection for directly-accessible JSP pages.

- **doFilter():** Checks `session.getAttribute("userId") != null`. If missing, redirects to `login.jsp`. If present, calls `chain.doFilter()` to pass through.
- Without this filter, users could navigate to `dashboard.jsp` or `friends.jsp` without logging in, since JSPs are not servlets and have no built-in auth.

---

## 6. How Files Connect — MVC Data Flow

```
Browser Request
      |
      v
  @WebServlet routing (e.g., /message -> MessageServlet)
      |
      v
  Servlet (Controller)
  |-- Reads & validates request parameters
  |-- Validates session
  +--> Calls Service layer
          |
          v
      Service (Business Logic)
      |-- Enforces business rules
      +--> Calls DAO layer
              |
              v
          DAO (Data Layer)
          |-- Opens connection via DBConnection.getConnection()
          |-- Executes PreparedStatement
          |-- Maps ResultSet rows to Model objects
          +--> Returns Model / List<Model> to Service
              |
              v
      Servlet receives Model objects
      |-- request.setAttribute("key", data)
      +--> Forwards to JSP
              |
              v
          JSP (View)
          |-- Reads request attributes
          |-- Generates HTML with Java scriptlets
          +--> Sends HTML response to browser
```

---

## 7. Common Issues and Solutions

| Symptom | Likely Cause | Fix |
|---|---|---|
| HTTP 500 on any page | Null session attribute accessed without check | Add `session.getAttribute("userId") != null` guard before use |
| Admin redirect loop | `"admin"` user not in database | INSERT admin user with BCrypt hash via `AppInitListener` or SQL |
| Messages not saving | Form action points to wrong URL | Ensure `action="message"` (not `message.jsp`) in chat.jsp form |
| Friend count shows 0 | SQL only checks `user_id1` | `AdminDAO` checks `user_id1 = u.id OR user_id2 = u.id` |
| "Driver not found" | H2 JAR scope is `provided` | Change `<scope>` to `compile` in pom.xml |
| Session lost between pages | Tomcat session timeout too short | Increase `session-timeout` in web.xml or call `setMaxInactiveInterval()` |

---

## 8. Codebase Line Count Statistics

> Counts measured across all tracked source files (excludes compiled `.class` files and the `target/` build directory).
> Generated: 2026-05-12

### Total Lines — All Source Files

| File Type | File Count | Total Lines |
|---|---|---|
| `.java` | 50 | 2,625 |
| `.jsp` | 17 | 1,566 |
| `.css` | 1 | 281 |
| `.md` | 4 | 774 |
| `.sql` | 1 | 78 |
| `.xml` | 1 | 48 |
| **Grand Total** | **74** | **5,372** |

### Java Files Only — Top 10 by Size

| File | Lines |
|---|---|
| `FriendRequestDAO.java` | 160 |
| `MessageServlet.java` | 152 |
| `AppInitListener.java` | 124 |
| `AdminDAO.java` | 112 |
| `UserDAO.java` | 109 |
| `ServerDAO.java` | 102 |
| `AdminServlet.java` | 93 |
| `FriendServlet.java` | 91 |
| `AuthService.java` | 88 |
| `FriendsDAO.java` | 85 |

**Total across all 50 `.java` files: 2,625 lines**
