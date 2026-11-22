
package com.yuvraj.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(
            String fullName,
            String email,
            String passwordHash,
            String role,
            String department,
            String phone) {

        String sql = "INSERT INTO users "
                + "(full_name, email, password_hash, role, department, phone) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, role);
            ps.setString(5, department);
            ps.setString(6, phone);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("User registration failed!");
            e.printStackTrace();
            return false;
        }
    }
}
