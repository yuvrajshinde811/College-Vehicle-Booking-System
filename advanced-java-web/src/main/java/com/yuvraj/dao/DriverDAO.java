package com.yuvraj.dao;

import com.yuvraj.database.DBConnection;
import com.yuvraj.model.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {

    public boolean addDriver(Driver driver) {
        String sql = "INSERT INTO drivers (driver_name, phone, license_number, status) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, driver.getDriverName());
            ps.setString(2, driver.getPhone());
            ps.setString(3, driver.getLicenseNumber().trim().toUpperCase());
            ps.setString(4, driver.getStatus() != null ? driver.getStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        driver.setDriverId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding driver: " + e.getMessage());
            return false;
        }
    }

    public Driver getDriverById(int driverId) {
        String sql = "SELECT driver_id, driver_name, phone, license_number, status, created_at "
                   + "FROM drivers WHERE driver_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, driverId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDriver(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching driver by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Driver> getAllDrivers() {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT driver_id, driver_name, phone, license_number, status, created_at "
                   + "FROM drivers ORDER BY driver_id ASC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToDriver(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all drivers: " + e.getMessage());
        }
        return list;
    }

    public List<Driver> getActiveDrivers() {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT driver_id, driver_name, phone, license_number, status, created_at "
                   + "FROM drivers WHERE status = 'ACTIVE' ORDER BY driver_name ASC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToDriver(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active drivers: " + e.getMessage());
        }
        return list;
    }

    /**
     * Finds active drivers who are NOT assigned to an overlapping trip.
     */
    public List<Driver> getAvailableDrivers(String startDateTime, String endDateTime) {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT d.driver_id, d.driver_name, d.phone, d.license_number, d.status, d.created_at "
                   + "FROM drivers d "
                   + "WHERE d.status = 'ACTIVE' "
                   + "  AND d.driver_id NOT IN ("
                   + "      SELECT t.driver_id FROM trips t "
                   + "      WHERE t.trip_status IN ('SCHEDULED', 'IN_PROGRESS') "
                   + "        AND (t.scheduled_start < ? AND t.scheduled_end > ?)"
                   + "  ) ORDER BY d.driver_name ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, endDateTime);
            ps.setString(2, startDateTime);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDriver(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking available drivers: " + e.getMessage());
        }
        return list;
    }

    public boolean updateDriver(Driver driver) {
        String sql = "UPDATE drivers SET driver_name = ?, phone = ?, license_number = ?, status = ? "
                   + "WHERE driver_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, driver.getDriverName());
            ps.setString(2, driver.getPhone());
            ps.setString(3, driver.getLicenseNumber().trim().toUpperCase());
            ps.setString(4, driver.getStatus());
            ps.setInt(5, driver.getDriverId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating driver: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateDriver(int driverId) {
        String sql = "UPDATE drivers SET status = 'INACTIVE' WHERE driver_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, driverId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating driver: " + e.getMessage());
            return false;
        }
    }

    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        java.sql.Timestamp createdAt = null;
        try {
            createdAt = rs.getTimestamp("created_at");
        } catch (SQLException ignored) {}

        return new Driver(
                rs.getInt("driver_id"),
                rs.getString("driver_name"),
                rs.getString("phone"),
                rs.getString("license_number"),
                rs.getString("status"),
                createdAt
        );
    }
}
