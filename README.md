# NUSTcord

**NUSTcord** is a comprehensive, Java-based Object-Oriented web application designed to simulate a structured communication and networking platform. It emphasizes clean code architecture, database security, and modularity without relying on heavy frameworks like Spring Boot. The platform enables secure user interactions, friend networking, server creation, channel-based discussions, role-based access management, and an admin dashboard for user oversight.

## Tech Stack
*   **Backend**: Java 11, Servlets 4.0
*   **Database**: Embedded H2 Database (File-based, MySQL-compatibility mode)
*   **Frontend**: HTML, CSS, JSP
*   **Security**: jBCrypt (Password Hashing)
*   **Build Tool**: Maven
*   **Server**: Apache Tomcat 9+

## UI Theme
The frontend implements a modern, card-based dark-mode aesthetic utilizing a strict color scheme:
*   **Dark background** (`#1a1b2e`) for depth.
*   **Light text** (`#e0e0ff`) for clear, readable typography.
*   **Purple** (`#5865f2` / `#7c3aed`) as the dynamic accent color.

## Architecture
The project strictly follows a layered MVC architecture to maintain high cohesion and low coupling.

```
NUSTcord/
├── src/main/java/com/nustcord/
│   ├── model/       # Java POJOs mapped to DB tables (User, Server, Channel, Message, etc.)
│   ├── dao/         # Data Access Objects executing SQL via PreparedStatements
│   ├── service/     # Business logic layer (Auth, Validation, Messaging)
│   ├── servlet/     # Controllers mediating between JSP views and Services
│   ├── filter/      # Request interception (AuthFilter for unauthenticated traffic)
│   ├── listener/    # App lifecycle hooks (AppInitListener — DB init & seed accounts)
│   ├── util/        # Helpers (DBConnection, PasswordUtil)
│   └── exception/   # Custom domain errors (AuthException)
└── src/main/webapp/
    ├── css/         # Global dark-theme stylesheet
    ├── js/          # Client-side JavaScript
    ├── includes/    # Reusable JSP fragments (top-nav, left-sidebar, user-controls)
    └── (JSPs)       # Dynamic views (login, register, dashboard, chat, friends,
                     #   serverList, directMessage, settings, adminDashboard, etc.)
```

For a deeper dive into every file and how they connect, see **[WALKTHROUGH.md](WALKTHROUGH.md)**.  
For OOP concepts applied in this codebase, see **[OOP.md](OOP.md)**.

## Core Features

### 1. Secure Authentication & User Identity
*   **Registration**: Users register with a unique username, email, and password.
*   **Password Security**: Plain-text passwords are never stored. `jBCrypt` is used to generate a 60-character salted hash, which is stored in the database and verified upon login.
*   **Session Management**: Logins tie the `userId` and `username` to a native `HttpSession`. Route protection via `AuthFilter` ensures unauthenticated users are redirected to the login page.
*   **Profiles & Status**: Users have editable display names and bios. Status tracking updates users as `Online`, `Offline`, `Busy`, or `Away` based on their session activity.
*   **Auto-Seeded Accounts**: On first startup, `AppInitListener` automatically creates two seed accounts (see [Test Credentials](#test-credentials) below) — no manual SQL required.

### 2. Admin Dashboard
*   **Access**: Log in with the admin credentials (see below). The admin is automatically redirected to `/admin` after login.
*   **User Table**: Displays every registered user with their ID, username, email, display name, registration date, online status, friend count, server count, and last login timestamp.
*   **Live Search & Filter**: Client-side search across username/email/display name and a status dropdown filter.
*   **Sortable Columns**: Click any column header to sort ascending/descending.
*   **Access Control**: Only the `admin` account can reach this page. Any other user attempting direct access is redirected to the login page with an "Unauthorized" error.

### 3. Friend Networking
*   **Friend Requests**: Users can send connection invites using a unique username. These requests transition through `PENDING`, `ACCEPTED`, or `REJECTED` states.
*   **Bidirectional Friendships**: Accepting a request establishes a permanent, bidirectional friendship in the database.
*   **Sent Requests Tracking**: A dedicated interface allows users to track the status of outgoing requests.

### 4. Server Management & Roles
*   **Servers**: Users can create and manage their own communities (servers). The creator is automatically designated as the server owner with the `Admin` role and a default `#general` text channel.
*   **Server Discovery**: Users can browse all servers and join ones they are not yet a member of.
*   **Role-Based Access**: Granular roles (e.g., Admin, Member) with configurable permissions govern what users can do within a server.

### 5. Channels & Messaging
*   **Text Channels**: Servers support multiple text channels. Messages are persisted in the database and rendered in chronological order.
*   **Direct Messaging**: A persistent, private messaging system allowing bidirectional, 1-on-1 chats with friends.
*   **Author Display**: Each message shows the sender's display name (falling back to their username if no profile is set).

## Database Schema
The backend operates on a relational embedded H2 database (`nustcord_db`). Key tables:
*   `users`, `profiles`, `user_status`
*   `friend_requests`, `friends`
*   `servers`, `channels`, `messages`, `direct_messages`
*   `roles`, `user_server_map`

*(The complete schema with constraints and foreign keys is in `schema.sql`)*

## Test Credentials

| Role | Username | Password | Notes |
|---|---|---|---|
| Regular user | `dummy` | `dummy` | Auto-created on first startup |
| Admin | `admin` | `password1` | Auto-created on first startup; redirects to `/admin` on login |

> Both accounts are inserted automatically by `AppInitListener` on the first Tomcat deployment. No manual SQL or `TestDB.java` run is needed.

## Documentation
| File | Contents |
|---|---|
| [WALKTHROUGH.md](WALKTHROUGH.md) | Full code walkthrough — request flows, key file explanations, MVC diagram, line count stats |
| [OOP.md](OOP.md) | OOP concepts (Classes, Encapsulation, Inheritance, Polymorphism, Abstraction, Exception Handling, SOLID) with real code examples |
| [Setup.md](Setup.md) | Step-by-step environment setup (Java, Maven, Tomcat 9) |
| [schema.sql](schema.sql) | Full database DDL |

## Setup & Installation

Please refer to the detailed [Setup.md](Setup.md) for a comprehensive, step-by-step guide.

### Quick Start
1. Ensure **Java 11+**, **Maven**, and **Apache Tomcat 9+** are installed (see [Setup.md](Setup.md)).
2. Clone the repo and open the project root (the folder containing `pom.xml`).
3. Run `mvn clean package` to build the `.war` file.
4. Rename `target/NUSTcord-1.0-SNAPSHOT.war` to `NUSTcord.war` and drop it into Tomcat's `webapps/` folder.
5. Start Tomcat (`startup.bat` on Windows / `startup.sh` on Linux/macOS).
6. The database schema and seed accounts are **created automatically** on first launch — no manual SQL step required.
7. Navigate to `http://localhost:8080/NUSTcord/login.jsp` and log in with `dummy` / `dummy` or `admin` / `password1`.

> **Note**: The project uses an embedded H2 database stored as a local file (`nustcord_db.mv.db`). No external database installation is required.
