package com.nustcord.filter;

/**
 * AuthFilter.java
 * Purpose: Servlet Filter that enforces authentication on protected pages.
 * Key Responsibilities:
 *  - Intercept all requests to guarded URL patterns before they reach the servlet
 *  - Check that a valid HTTP session with a "userId" attribute exists
 *  - Redirect unauthenticated requests to login.jsp with an error message
 *  - Allow authenticated requests to continue through the filter chain
 * Created: 2026-05-12
 */

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Protects sensitive pages from unauthenticated access.
 * Applied to dashboard.jsp, profile.jsp, friends.jsp, and all /app/* URLs.
 * The adminDashboard is additionally protected by AdminServlet's own guard.
 *
 * Note: This filter does NOT protect servlet URLs like /message or /server –
 * those servlets perform their own session checks internally.
 */
@WebFilter(urlPatterns = {"/dashboard.jsp", "/profile.jsp", "/friends.jsp", "/app/*"})
public class AuthFilter implements Filter {

    /**
     * Called once when the filter is first loaded by the servlet container.
     * No initialisation needed for this stateless filter.
     *
     * @param filterConfig provided by the container (unused here)
     * @throws ServletException if initialisation fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * Core filter logic: checks the session, then either forwards the request
     * or redirects to the login page.
     *
     * @param request  the incoming HTTP request (cast to HttpServletRequest)
     * @param response the outgoing HTTP response (cast to HttpServletResponse)
     * @param chain    the remainder of the filter + servlet chain
     * @throws IOException      if the redirect fails
     * @throws ServletException if chain.doFilter() throws
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Cast to HTTP-specific types so we can inspect the session and send redirects
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // getSession(false) returns null if no session exists; avoids creating an empty session
        HttpSession session = req.getSession(false);

        // A user is considered "logged in" only if a session exists AND userId is set.
        // userId is set in LoginServlet upon successful authentication.
        boolean loggedIn = (session != null && session.getAttribute("userId") != null);

        if (loggedIn) {
            // Valid session – allow the request to continue to the target resource
            chain.doFilter(request, response);
        } else {
            // No valid session – redirect to login with a user-friendly message.
            // getContextPath() ensures the redirect works in any deployment context.
            res.sendRedirect(req.getContextPath() + "/login.jsp?error=Please log in first.");
        }
    }

    /**
     * Called once when the application shuts down.
     * No cleanup needed for this stateless filter.
     */
    @Override
    public void destroy() {}
}
