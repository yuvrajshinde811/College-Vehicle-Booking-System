package com.yuvraj.dao;

import com.yuvraj.database.DBConnection;
import com.yuvraj.database.PasswordUtil;
import com.yuvraj.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO handles JDBC CRUD operations for user accounts,
 * registration, authentication, and profile lookups.
 */
public class UserDAO {

    /**
     * Preserved signature: Registers a new user with raw string arguments.
     */
    public boolean registerUser(
            String fullName,
            String email,
            String passwordHash,
            String role,
            String department,
            String phone) {
        User user = new User(fullName, email, passwordHash, role, department, phone);
        return registerUser(user);
    }

    /**
     * Registers a new user using a User model object.
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, department, phone, account_status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().toUpperCase());
            ps.setString(5, user.getDepartment());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getAccountStatus() != null ? user.getAccountStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("User registration failed for email " + user.getEmail() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates a user by email and plain-text password using PasswordUtil.
     * @return User object if authentication is successful; null otherwise.
     */
    public User authenticate(String email, String plainPassword) {
        String sql = "SELECT user_id, full_name, email, password_hash, role, department, phone, account_status, created_at "
                   + "FROM users WHERE email = ? AND account_status = 'ACTIVE'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordUtil.verifyPassword(plainPassword, storedHash)) {
                        return mapResultSetToUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication query error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if an email address is already registered.
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a user by primary key ID.
     */
    public User getUserById(int userId) {
        String sql = "SELECT user_id, full_name, email, password_hash, role, department, phone, account_status, created_at "
                   + "FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all registered users.
     */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, full_name, email, password_hash, role, department, phone, account_status, created_at "
                   + "FROM users ORDER BY user_id ASC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return list;
    }

    /**
     * Updates an existing user's profile details.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, department = ?, phone = ?, role = ?, account_status = ? "
                   + "WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getDepartment());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getAccountStatus());
            ps.setInt(6, user.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Soft deletes / deactivates a user account.
     */
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET account_status = 'INACTIVE' WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("department"),
                rs.getString("phone"),
                rs.getString("account_status"),
                rs.getTimestamp("created_at")
        );
    }
}
