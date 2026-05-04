# NUSTcord

**NUSTcord** is a comprehensive, Java-based Object-Oriented web application designed to simulate a structured communication and networking platform. It emphasizes clean code architecture, database security, and modularity without relying on heavy frameworks like Spring Boot. The platform enables secure user interactions, friend networking, server creation, channel-based discussions, and role-based access management.

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
│   ├── model/       # Java classes mapped to DB Tables (User, Profile, Server, Channel, Message, etc.)
│   ├── dao/         # Data Access Objects executing SQL via PreparedStatements
│   ├── service/     # Business logic layer (Auth logic, Validation, Messaging)
│   ├── servlet/     # Controllers mediating between UI (JSPs) and Services
│   ├── filter/      # Request interception (AuthFilter routing unauthenticated traffic)
│   ├── util/        # Helpers (DBConnection, PasswordUtil)
│   └── exception/   # Custom domain errors
└── src/main/webapp/
    ├── css/         # Global stylesheets 
    ├── (JSPs)       # Dynamic frontend views (chat, dashboard, serverList, etc.)
```

## ✨ Core Features

### 1. Secure Authentication & User Identity
*   **Registration**: Users register with a unique username, email, and password.
*   **Password Security**: Plain-text passwords are never stored. `jBCrypt` is used to generate a 60-character salted hash, which is stored in the database and verified upon login.
*   **Session Management**: Logins tie the `userId` to a native `HttpSession`. Route protection ensures unauthenticated users are seamlessly redirected.
*   **Profiles & Status**: Users have editable display names and bios. Real-time status tracking updates users as `Online`, `Offline`, `Busy`, or `Away` based on their session activity.

### 2. Friend Networking
*   **Friend Requests**: Users can send connection invites using a unique username. These requests transition through `PENDING`, `ACCEPTED`, or `REJECTED` states.
*   **Bidirectional Friendships**: Accepting a request establishes a permanent, bidirectional friendship in the database.
*   **Sent Requests Tracking**: A dedicated interface allows users to track the status of outgoing requests.

### 3. Server Management & Roles
*   **Servers**: Users can create and manage their own communities (servers). The creator is automatically designated as the server owner with administrative privileges.
*   **User Memberships**: The application tracks server memberships, allowing users to switch between multiple servers dynamically via their dashboard.
*   **Role-Based Access**: Granular roles (e.g., Admin, Member) with specific comma-separated permissions (`kick`, `ban`, `delete_message`) govern what users can do within a server.

### 4. Channels & Messaging
*   **Channels**: Servers support multiple distinct communication channels categorized into **Text Channels** (for chat messages) and **Voice Channels** (for audio rooms).
*   **Chat System**: Text channels feature a messaging system where users can send messages. These are time-stamped and ordered chronologically to maintain history.
*   **Chat View**: Dedicated views dynamically load historical messages and allow seamless posting for members of the channel.

## 🗄️ Database Schema
The backend operates on a tightly-coupled relational database (`nustcord_db`). Key tables include:
*   `users`, `profiles`, `user_status`
*   `friend_requests`, `friends`
*   `servers`, `channels`, `messages`
*   `roles`, `user_server_map`

*(The complete schema definition with constraints and relations can be found in `schema.sql`)*.

## 🛠️ Setup & Installation

Please refer to the detailed [Setup.md](Setup.md) for a comprehensive, step-by-step guide to installing and running the application using Java JDK, Tomcat 9, and Maven.

### Quick Start
1. Ensure MySQL is installed and running.
2. Execute the included `schema.sql` file to build the database structure.
3. Update the `DBConnection.java` file in the `util` package to match your local MySQL credentials.
4. Run `mvn clean package` in the project root to pull libraries and construct the `.war` configuration.
5. Deploy the application to an application server, such as **Apache Tomcat 9+**.
6. Navigate to `http://localhost:8080/NUSTcord/login.jsp` to access the application.
