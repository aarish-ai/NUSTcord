# NUSTcord — Object-Oriented Programming Concepts

> This document explains how the six core OOP pillars (plus SOLID principles)
> are applied **concretely** in the NUSTcord codebase, with real code examples.

---

## 1. Classes and Objects

**Definition:** A *class* is a blueprint that defines structure and behaviour.
An *object* is a concrete instance of that blueprint with actual data.

**In NUSTcord**, every database entity is modelled as a class:

```java
// User.java — blueprint for every registered account
public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private Timestamp createdAt;

    // Constructor creates a User object with actual values
    public User(int id, String username, String email,
                String passwordHash, Timestamp createdAt) {
        this.id           = id;
        this.username     = username;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.createdAt    = createdAt;
    }
}
```

When `UserDAO.getUserByUsername("alice")` runs, it executes SQL and then calls
`new User(rs.getInt("id"), ...)` — creating one concrete **object** from the **class** blueprint.

**Other classes in NUSTcord:**
- `Server.java` — represents a single Discord-like server community
- `Message.java` — represents one chat message sent in a channel
- `Channel.java` — represents a text or voice channel inside a server
- `AdminDAO.AdminUserRow` — inner class used exclusively for the admin dashboard

---

## 2. Encapsulation

**Definition:** Hiding internal data by making fields `private` and exposing
controlled access through `public` getters and setters.

**Implementation in NUSTcord:**

```java
// User.java — all fields are private; access is controlled
public class User {
    // Private fields — cannot be modified from outside this class
    private int id;
    private String passwordHash;   // Especially sensitive — never expose raw

    // Public getter — read-only access to the ID
    public int getId() {
        return id;
    }

    // Public getter for the hash — callers get the value but can't change it
    // without going through a setter that could validate the new value
    public String getPasswordHash() {
        return passwordHash;
    }

    // Setter — could add validation here (e.g., reject null hashes)
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
```

**Practical benefit:** If you later decide to encrypt the stored hash before
setting it, you only change the `setPasswordHash()` method — all callers
continue working without modification.

**Where we use it:**
- All model classes: `User`, `Server`, `Message`, `Channel`, `Profile`, `Role`
- `AdminDAO.AdminUserRow` public fields are a deliberate exception — it is an
  internal data-holder not exposed outside the package.

---

## 3. Inheritance

**Definition:** A child class inherits fields and methods from a parent class,
enabling code reuse and the extension of existing behaviour.

**Implementation in NUSTcord:**
All servlets extend `HttpServlet` from the Java Servlet API:

```java
// Every servlet in the project inherits from HttpServlet
public class LoginServlet extends HttpServlet {
    // Inherits: service(), doGet(), doPost(), init(), destroy(), getServletContext()...
    // We only override the methods we need

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        // Our custom login logic — the rest of HttpServlet's behaviour is inherited
    }
}

// Another child of HttpServlet — inherits the same lifecycle methods
public class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        // Admin-specific logic
    }
}
```

**What is inherited from `HttpServlet`:**
- `init()` — called once when Tomcat loads the servlet; we override this in
  `MessageServlet` and `ServerServlet` to instantiate service dependencies.
- `service()` — dispatches to `doGet` or `doPost` automatically based on HTTP verb.
- `destroy()` — called on shutdown; we override this in filter classes if needed.

**Where we use it:**
- Every servlet class (`LoginServlet`, `AdminServlet`, `MessageServlet`, etc.) extends `HttpServlet`
- `AuthFilter` implements the `Filter` interface (interface-based contract)
- `AuthException` extends `Exception` to create a custom, domain-specific error type

---

## 4. Polymorphism

**Definition:** The same method name produces different behaviour depending on
the runtime type of the object. Enables flexible, interchangeable code.

**Implementation in NUSTcord — method overriding:**
Every servlet overrides `doGet()` and/or `doPost()` differently:

```java
// MessageServlet.doGet() loads messages for a channel
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    // Fetches messages from DB and forwards to chat.jsp
}

// DirectMessageServlet.doGet() loads DM conversation messages
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    // Fetches DMs from DB and forwards to directMessage.jsp
}

// The Servlet container calls doGet() on whichever servlet matches the URL.
// The correct implementation runs automatically — this is runtime polymorphism.
```

**Compile-time polymorphism — method overloading:**

```java
// ServerService.java has two constructors with different parameter lists
public class ServerService {

    // Constructor used in production code (no-arg, creates its own DAOs)
    public ServerService() {
        this.serverDAO       = new ServerDAO();
        this.userServerMapDAO = new UserServerMapDAO();
        // ...
    }

    // Constructor used for testing or when DAOs are injected externally
    public ServerService(ServerDAO serverDAO, UserServerMapDAO userServerMapDAO) {
        this.serverDAO        = serverDAO;
        this.userServerMapDAO = userServerMapDAO;
        // ...
    }
}
```

**Where we use it:**
- HTTP verb dispatch (`doGet`/`doPost`) across all servlet classes
- Constructor overloading in `ServerService` and `User`
- `FriendService.acceptRequest()` vs `FriendService.rejectRequest()` — same
  signature shape, different database outcomes

---

## 5. Abstraction

**Definition:** Hiding complex implementation details behind a simple interface,
so callers don't need to know *how* something works — only *what* it does.

**Implementation in NUSTcord — the DAO layer:**

```java
// From a servlet's perspective, saving a message is a single method call:
messageService.sendMessage(msg);

// The servlet has NO IDEA that behind this one line, MessageService does:
public void sendMessage(Message message) throws SQLException {
    // Step 1: open a JDBC connection
    // Step 2: prepare a parameterized SQL INSERT statement
    // Step 3: bind channel_id, sender_id, and content parameters
    // Step 4: execute the INSERT
    // Step 5: retrieve the auto-generated primary key
    // Step 6: set the key back on the message object
    // Step 7: close all resources via try-with-resources
    messageDAO.saveMessage(message);
}
```

The servlet calls `sendMessage(msg)` — a simple, human-readable interface.
All the JDBC boilerplate is abstracted away in the DAO layer.

**Another example — DBConnection:**

```java
// Every DAO just calls this one method to get a connection
Connection conn = DBConnection.getConnection();

// The callers don't know or care that DBConnection:
// - loaded the H2 JDBC driver in a static initializer block
// - assembled the JDBC URL with MySQL compatibility flags
// - called DriverManager.getConnection() under the hood
```

**Benefits:**
- Servlets don't need SQL knowledge — they use service methods with meaningful names
- Swapping from H2 to MySQL only requires changing `DBConnection.java` — zero changes in DAOs

---

## 6. Exception Handling

**Definition:** Structured error management that prevents crashes and provides
meaningful feedback to users and developers.

**Custom exception — `AuthException.java`:**

```java
// AuthService throws AuthException instead of a generic Exception,
// making it clear at the call site exactly what kind of error occurred.
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
```

**Layered handling in LoginServlet:**

```java
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        try {
            // Attempt login — may throw AuthException on bad credentials
            User loggedInUser = authService.login(user, pass);

            // Success path: create session and redirect
            request.getSession().setAttribute("userId", loggedInUser.getId());
            response.sendRedirect("loading.jsp");

        } catch (AuthException e) {
            // Known, expected failure — show a user-friendly error message
            // Never expose internal stack traces to the browser
            response.sendRedirect("login.jsp?error=" + e.getMessage());
        }
        // No catch for unexpected RuntimeException — let the container handle it
    }
}
```

**Database error handling in AdminServlet:**

```java
try {
    List<AdminUserRow> users = adminDAO.getAllUsersWithStats();
    request.setAttribute("users", users);

} catch (SQLException e) {
    // Database-specific error: log internally, show friendly message in JSP
    System.err.println("[AdminServlet] Database error: " + e.getMessage());
    request.setAttribute("dbError", "Could not load user data: " + e.getMessage());
    // Note: we do NOT rethrow — this ensures no HTTP 500 is returned
}

// Forward to JSP regardless of success or failure
request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
```

**Where we use it:**
- `AuthException` in `AuthService`, `LoginServlet`, `RegisterServlet`
- `SQLException` catching in every DAO method
- `NumberFormatException` catching when parsing URL integer parameters
- `NullPointerException` prevention via explicit null checks on all session attributes

---

## 7. SOLID Principles

**S — Single Responsibility Principle**
Each class has exactly one reason to change:
- `UserDAO` only does user database operations — not business logic
- `LoginServlet` only handles the login HTTP flow — not DB queries
- `AuthService` only contains authentication business logic — not HTTP concerns
- `User.java` only holds user data — no methods that query the database

**O — Open/Closed Principle**
Classes are open for extension but closed for modification:
- Adding a new message type (e.g., image messages) means creating a new DAO method,
  not modifying existing `MessageDAO` logic
- New servlet actions are added as new `if` branches in `FriendServlet`, not by
  rewriting existing action handlers

**L — Liskov Substitution Principle**
Child classes can replace their parent without breaking behaviour:
- `LoginServlet`, `AdminServlet`, and `MessageServlet` all extend `HttpServlet`
  and can be substituted wherever an `HttpServlet` is expected (e.g., in Tomcat's
  servlet registry) without surprising side effects

**I — Interface Segregation Principle**
DAOs are narrow and purpose-specific — not one giant "catch-all" class:
- `UserDAO` only has user methods (`registerUser`, `getUserByUsername`, `getUserById`)
- `FriendRequestDAO` only has friend-request methods — no overlap with `FriendsDAO`
- `MessageDAO` only handles channel messages — `DirectMessageDAO` handles DMs separately

**D — Dependency Inversion Principle**
High-level modules depend on abstractions, not concrete implementations:
- `ServerServlet` receives a `ServerDAO` object in `init()` — it doesn't construct one itself
- `MessageService` receives a `MessageDAO` in its constructor, making it easy to
  substitute a mock DAO during testing:

```java
// Production code:
messageService = new MessageService(new MessageDAO());

// Test code (hypothetical):
messageService = new MessageService(new MockMessageDAO());
// MockMessageDAO could simulate DB errors or return preset data
```
