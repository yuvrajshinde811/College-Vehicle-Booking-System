package com.yuvraj.dao;

import com.yuvraj.database.DBConnection;
import com.yuvraj.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (vehicle_number, vehicle_type, capacity, status) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, vehicle.getVehicleNumber().trim().toUpperCase());
            ps.setString(2, vehicle.getVehicleType());
            ps.setInt(3, vehicle.getCapacity());
            ps.setString(4, vehicle.getStatus() != null ? vehicle.getStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        vehicle.setVehicleId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            return false;
        }
    }

    public Vehicle getVehicleById(int vehicleId) {
        String sql = "SELECT vehicle_id, vehicle_number, vehicle_type, capacity, status, created_at "
                   + "FROM vehicles WHERE vehicle_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vehicle by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT vehicle_id, vehicle_number, vehicle_type, capacity, status, created_at "
                   + "FROM vehicles ORDER BY vehicle_id ASC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all vehicles: " + e.getMessage());
        }
        return list;
    }

    public List<Vehicle> getActiveVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT vehicle_id, vehicle_number, vehicle_type, capacity, status, created_at "
                   + "FROM vehicles WHERE status = 'ACTIVE' ORDER BY capacity ASC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active vehicles: " + e.getMessage());
        }
        return list;
    }

    /**
     * Finds active vehicles with sufficient capacity that are NOT booked for overlapping trips.
     */
    public List<Vehicle> getAvailableVehicles(int minCapacity, String startDateTime, String endDateTime) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT v.vehicle_id, v.vehicle_number, v.vehicle_type, v.capacity, v.status, v.created_at "
                   + "FROM vehicles v "
                   + "WHERE v.status = 'ACTIVE' AND v.capacity >= ? "
                   + "  AND v.vehicle_id NOT IN ("
                   + "      SELECT t.vehicle_id FROM trips t "
                   + "      WHERE t.trip_status IN ('SCHEDULED', 'IN_PROGRESS') "
                   + "        AND (t.scheduled_start < ? AND t.scheduled_end > ?)"
                   + "  ) ORDER BY v.capacity ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, minCapacity);
            ps.setString(2, endDateTime);
            ps.setString(3, startDateTime);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking available vehicles: " + e.getMessage());
        }
        return list;
    }

    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET vehicle_number = ?, vehicle_type = ?, capacity = ?, status = ? "
                   + "WHERE vehicle_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, vehicle.getVehicleNumber().trim().toUpperCase());
            ps.setString(2, vehicle.getVehicleType());
            ps.setInt(3, vehicle.getCapacity());
            ps.setString(4, vehicle.getStatus());
            ps.setInt(5, vehicle.getVehicleId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateVehicle(int vehicleId) {
        String sql = "UPDATE vehicles SET status = 'INACTIVE' WHERE vehicle_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating vehicle: " + e.getMessage());
            return false;
        }
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        java.sql.Timestamp createdAt = null;
        try {
            createdAt = rs.getTimestamp("created_at");
        } catch (SQLException ignored) {}

        return new Vehicle(
                rs.getInt("vehicle_id"),
                rs.getString("vehicle_number"),
                rs.getString("vehicle_type"),
                rs.getInt("capacity"),
                rs.getString("status"),
                createdAt
        );
    }
}
