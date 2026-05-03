# NUSTcord - Phase A

**NUSTcord** is a Java OOP web application built to simulate a structured communication and networking platform. It emphasizes clean code architecture, database security, and modularity without relying on heavy frameworks like Spring Boot.

This repository currently contains **Phase A**, demonstrating core user identity management, status tracking, and friend request networking.

## 🚀 Tech Stack
*   **Backend**: Java 11, Servlets 4.0
*   **Database**: MySQL (accessed via native JDBC)
*   **Frontend**: HTML, CSS, JSP
*   **Security**: jBCrypt (Password Hashing)
*   **Build Tool**: Maven

## 🎨 UI Theme
The frontend implements a modern, card-based aesthetic utilizing a strict color scheme:
*   **Black** (`#121212`) for depth and background.
*   **Silver** (`#e0e0e0`) for clear, readable typography.
*   **Purple** (`#8a2be2`) as the dynamic accent color.

## 🏗️ Architecture
The project strictly follows a layered architecture to maintain high cohesion and low coupling. Most files are kept strictly under 80 lines to emphasize modularity.

```
NUSTcord/
├── src/main/java/com/nustcord/
│   ├── model/       # Java classes mapped to DB Tables (User, Profile, FriendRequest)
│   ├── dao/         # Data Access Objects executing SQL via PreparedStatements
│   ├── service/     # Business logic layer (Auth logic, Friend request validation)
│   ├── servlet/     # Controllers mediating between UI (JSPs) and Services
│   ├── filter/      # Request interception (AuthFilter routing unauthenticated traffic)
│   ├── util/        # Helpers (DBConnection, PasswordUtil)
│   └── exception/   # Custom domain errors
└── src/main/webapp/
    ├── css/         # Global stylesheets 
    ├── (JSPs)       # Dynamic frontend views 
```

## ✨ Features Built in Phase A

### 1. Secure Authentication
*   **Registration**: Users register with a unique username, email, and password.
*   **Password Hashing**: We NEVER store plain-text passwords. Raw passwords are piped into `jBCrypt` to generate a 60-character salted hash, which is stored in the database. When logging in, the hashed output is strictly compared.
*   **Session Management**: Successful logins tie the `userId` to a native `HttpSession`.
*   **Route Protection**: An `AuthFilter` protects all `/*.jsp` files (except login and register). Trying to visit `dashboard.jsp` without a session automatically bounces the user to the login page.

### 2. User Profiles & Status
*   **Profiles**: Automatically handles editable display names and bios linked to a user via Foreign Keys.
*   **Status Tracking**: Users can mark themselves as `Online`, `Offline`, `Busy`, or `Away`. `LoginServlet` automatically sets them to `Online`, and `LogoutServlet` ensures they are shifted to `Offline` upon exit.

### 3. Friend Networking
*   **Friend Requests**: Allows sending a target user an invite using their unique **username**. A state machine transitions this from `PENDING` -> `ACCEPTED` / `REJECTED`. 
*   **Sent Requests Tracking**: Users can conveniently track the status of requests they have sent via a dedicated "Sent Requests" section.
*   **Validations**: Users cannot send friend requests to themselves or to non-existent usernames.
*   **Friendships**: Accepting a request triggers a dual-insert into the `friends` table ensuring a bidirectional and permanent connection.

## 🛠️ Setup & Installation

Please refer to the detailed [Setup.md](Setup.md) for a comprehensive, step-by-step guide to installing and running the application using Java JDK, Tomcat 9, and Maven.

### Quick Start
1. Ensure MySQL is installed.
2. Execute the included `schema.sql` file to build the `nustcord_db` schema. This handles all table relations (`users`, `profiles`, `user_status`, `friend_requests`, `friends`) with appropriate cascading deletions.
3. If necessary, update the `DBConnection.java` file in the `util` package to match your local MySQL username (default: `root`) and password (default: `password`).

### Running the Application
Since this project uses Maven:
1. Open your terminal at the root of the project.
2. Run `mvn clean package` to pull libraries and construct the `.war` configuration.
3. Deploy the application using any standard web container, such as **Apache Tomcat** (Version 9+ recommended).
    * _If using an IDE like IntelliJ IDEA or Eclipse, you can simply open the `pom.xml`, configure a local Tomcat Server Run Configuration, and click Play._
4. Navigate to `http://localhost:8080/NUSTcord/login.jsp` to begin.









# NUSTcord – Phase B
This phase extends Phase A by introducing **server, channel, and role management** features.  
It builds on the existing user, profile, and friends modules created in Phase A.

---

## 🚀 Features Implemented
### 1. Servers
- Users can create servers.
- Each server has an owner and members.
- Owner is automatically assigned an admin role.

### 2. Channels
- Servers can contain multiple channels.
- Two types supported:
  - **Text Channels** – for chat messages.
  - **Voice Channels** – for audio communication.
- Channels are linked to their parent server.

### 3. Roles
- Roles define permissions (e.g., `kick`, `ban`, `delete_message`).
- Roles are assigned per server.
- Permissions are stored as comma‑separated values.

### 4. User–Server Mapping
- Tracks which users belong to which servers.
- Stores the role assigned to each user in a server.

---

## 🗄️ Database Schema
```sql
CREATE TABLE servers (...);
CREATE TABLE channels (...);
CREATE TABLE roles (...);
CREATE TABLE user_server_map (...);

(See schema.sql for full definitions.)
---

###📂 Project Structure
src/main/java/com/nustcord/
 ├── model/
 │    ├── Server.java
 │    ├── Channel.java
 │    ├── ChannelType.java
 │    └── Role.java
 ├── dao/
 │    ├── ServerDAO.java
 │    ├── ChannelDAO.java
 │    ├── RoleDAO.java
 │    └── UserServerMapDAO.java
 ├── service/
 │    ├── ServerService.java
 │    ├── ChannelService.java
 │    └── RoleService.java
 └── servlet/
      ├── ServerServlet.java
      ├── ChannelServlet.java
      └── RoleServlet.java

---
###🖥️ User Interface
serverList.jsp → List servers and create new ones.
channelView.jsp → View and add channels for a server.
serverSettings.jsp → Manage roles for a server.
dashboard.jsp → Updated navigation with dynamic server links.
---
###📸 Sketches
Dashboard Navigation
[ Profile ] [ Friends ] [ Servers ]
    ├── Settings: Server A
    └── Channels: Server A
    ├── Settings: Server B
    └── Channels: Server B
[ Logout ]

Server–Channel–Role Relationship
Server
 ├── Channel (Text)
 ├── Channel (Voice)
 └── Roles
      ├── Admin (kick, ban)
      └── Member (read, write)

---
###✅ Testing Checklist
Create a server → appears in DB and UI.
Add channels → linked to server.
Add roles → visible in server settings.
Dashboard → dynamic links for each server.
Permissions → verify with RoleService.hasPermission().
---
###📌 Notes
No web.xml edits required (annotation‑based servlets).
Consistent modular design with Phase A.
Ready for extension into Phase C (messaging, notifications).


4. Run `mvn clean package` to build the `.war` file.
5. Deploy the application using **Apache Tomcat**.
6. Navigate to `http://localhost:8080/NUSTcord/login.jsp` to begin.
---
















# NUSTcord – Phase C

This phase extends Phase B by introducing a **messaging system** inside servers and channels.  
It allows users to send, store, and view messages, completing the core functionality of NUSTcord.

---

## Features Implemented

### Messaging
- Users can send text messages inside channels.
- Messages are linked to both the sender and the channel.
- Each message is timestamped for ordering.

### Channel Chat View
- Channels now display their message history.
- New messages appear at the bottom of the chat.
- Supports multiple users posting in the same channel.

### Database Integration
- Messages are stored in the `messages` table.
- Linked to `channels` and `users` via foreign keys.

---

## Database Schema

```sql
CREATE TABLE messages (
  id INT AUTO_INCREMENT PRIMARY KEY,
  channel_id INT NOT NULL,
  sender_id INT NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (channel_id) REFERENCES channels(id),
  FOREIGN KEY (sender_id) REFERENCES users(id)
);
```

---

## Project Structure

```
src/main/java/com/nustcord/
 ├── model/
 │    └── Message.java
 ├── dao/
 │    └── MessageDAO.java
 ├── service/
 │    └── MessageService.java
 └── servlet/
      └── MessageServlet.java
src/main/webapp/
 └── chat.jsp
```

---

## User Interface
- **chat.jsp** → Displays messages for a channel and allows posting new ones.
- Integrated into `channelView.jsp` with a link to open chat.

---

## Sketches

### Channel Chat View
```
Channel: General Chat
---------------------------------
[User1] Hello everyone!
[User2] Hi, welcome to the server.
[User3] Let's start our discussion.
---------------------------------
[ Message Input Box ] [ Send ]
```

### Data Flow
```
User → MessageServlet → MessageService → MessageDAO → Database
Database → MessageDAO → MessageService → chat.jsp → User
```

---

## Testing Checklist
1. Open a channel → chat.jsp loads messages from DB.
2. Send a message → appears in DB and UI.
3. Multiple users → messages ordered by timestamp.
4. Refresh → chat history persists.
5. Permissions → only server members can post.

---

## Notes
- Messaging is text‑based in Phase C.
- Future extension: add file sharing, reactions, or real‑time updates (AJAX/WebSockets).
- Completes the core functionality of NUSTcord across Phases A, B, and C.
```
