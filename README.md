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
*   **Validations**: Users cannot send friend requests to themselves.
*   **Friend Requests**: Allows sending a target user an invite. A state machine transitions this from `PENDING` -> `ACCEPTED` / `REJECTED`. 
*   **Friendships**: Accepting a request triggers a dual-insert into the `friends` table ensuring a bidirectional and permanent connection.

## 🛠️ Setup & Installation

### Database Configuration
1. Install MySQL.
2. Execute the included `schema.sql` file to build the `nustcord_db` schema. This handles all table relations (`users`, `profiles`, `user_status`, `friend_requests`, `friends`) with appropriate cascading deletions.
3. If necessary, update the `DBConnection.java` file in the `util` package to match your local MySQL username (default: `root`) and password (default: `password`).

### Running the Application
Since this project uses Maven:
1. Open your terminal at the root of the project.
2. Run `mvn clean package` to pull libraries and construct the `.war` configuration.
3. Deploy the application using any standard web container, such as **Apache Tomcat** (Version 9+ recommended).
    * _If using an IDE like IntelliJ IDEA or Eclipse, you can simply open the `pom.xml`, configure a local Tomcat Server Run Configuration, and click Play._
4. Navigate to `http://localhost:8080/NUSTcord/login.jsp` to begin.
