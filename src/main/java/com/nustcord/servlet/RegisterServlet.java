package com.nustcord.servlet;

/**
 * RegisterServlet.java
 * Purpose: Handles new user account creation.
 * Key Responsibilities:
 *  - Read username, email, and password from the registration form
 *  - Delegate to AuthService to validate, hash, and store the new user
 *  - Redirect to login on success, or back to register.jsp on failure
 * Created: 2026-05-12
 */

import com.nustcord.exception.AuthException;
import com.nustcord.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Processes POST submissions from register.jsp.
 * Maps to /RegisterServlet – the URL used in the registration form's action attribute.
 * Relies on AuthService to enforce business rules (username length, duplicate check, etc.)
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    // AuthService handles credential validation, BCrypt hashing, and DB insertion
    private final AuthService authService = new AuthService();

    /**
     * Handles the HTTP POST submission from the registration form.
     * Steps:
     *  1. Read username, email, password from POST parameters
     *  2. Call authService.register() – throws AuthException on validation failure
     *  3. On success: redirect to login.jsp with a success message
     *  4. On AuthException: redirect back to register.jsp with the error message
     *  5. If register() returns false (unlikely path): show generic failure message
     *
     * @param request  contains form parameters: "username", "email", "password"
     * @param response used to redirect the user after the operation
     * @throws ServletException propagated from underlying layers
     * @throws IOException      if the redirect fails
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Read all three required registration fields from the form
        String user  = request.getParameter("username");
        String email = request.getParameter("email");
        String pass  = request.getParameter("password");

        try {
            // Attempt registration through the service layer.
            // AuthService will validate inputs and throw AuthException on any rule violation.
            if (authService.register(user, email, pass)) {
                // Success – send the user to login with a confirmation message
                response.sendRedirect("login.jsp?success=Registration successful. Please log in.");
            } else {
                // Unexpected false return (e.g., DB INSERT returned 0 rows)
                response.sendRedirect("register.jsp?error=Registration failed.");
            }
        } catch (AuthException e) {
            // AuthException carries a user-readable message (e.g., "Username already taken")
            response.sendRedirect("register.jsp?error=" + e.getMessage());
        }
    }
}
