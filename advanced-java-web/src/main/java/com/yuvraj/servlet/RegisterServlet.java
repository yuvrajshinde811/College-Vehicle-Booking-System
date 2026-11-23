package com.yuvraj.servlet;

import com.yuvraj.dao.UserDAO;
import com.yuvraj.database.PasswordUtil;
import com.yuvraj.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role"); // STUDENT or STAFF
        String department = request.getParameter("department");
        String phone = request.getParameter("phone");

        // Basic validation
        if (fullName == null || fullName.isBlank() || email == null || email.isBlank()
                || password == null || password.isBlank() || role == null || role.isBlank()) {
            request.setAttribute("error", "All mandatory fields must be filled.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Restrict public registration to STUDENT or STAFF
        if (!"STUDENT".equalsIgnoreCase(role) && !"STAFF".equalsIgnoreCase(role)) {
            request.setAttribute("error", "Invalid registration role selected.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters long.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Check if email already registered
        if (userDAO.emailExists(email.trim())) {
            request.setAttribute("error", "Email is already registered. Please login or use a different email.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("department", department);
            request.setAttribute("phone", phone);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Hash password securely with PBKDF2
        String passwordHash = PasswordUtil.hashPassword(password);
        User newUser = new User(fullName.trim(), email.trim().toLowerCase(), passwordHash, role.toUpperCase(), department.trim(), phone.trim());

        boolean success = userDAO.registerUser(newUser);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?registered=true");
        } else {
            request.setAttribute("error", "Registration failed due to a database error. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
