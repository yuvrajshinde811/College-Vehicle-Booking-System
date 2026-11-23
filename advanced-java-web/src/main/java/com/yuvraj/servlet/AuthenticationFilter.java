package com.yuvraj.servlet;

import com.yuvraj.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * AuthenticationFilter intercepts requests to protected paths:
 * 1. Checks if the user is authenticated in the session.
 * 2. Restricts /admin/* paths strictly to users with role 'ADMIN'.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/dashboard", "/booking/*", "/admin/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // 1. Check if user is logged in
        if (user == null) {
            httpResponse.sendRedirect(contextPath + "/login.jsp?error=Please log in to continue");
            return;
        }

        // 2. Role-based security check for admin paths
        if (requestURI.startsWith(contextPath + "/admin/")) {
            if (!user.isAdmin()) {
                httpResponse.sendRedirect(contextPath + "/dashboard?error=Access denied: Administrator privileges required");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
