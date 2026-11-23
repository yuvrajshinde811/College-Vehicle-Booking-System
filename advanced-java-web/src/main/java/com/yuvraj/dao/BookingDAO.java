package com.yuvraj.dao;

import com.yuvraj.database.DBConnection;
import com.yuvraj.model.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingDAO {

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO booking_requests (user_id, purpose, source, destination, journey_date, "
                   + "requested_start_time, requested_end_time, passenger_count, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getUserId());
            ps.setString(2, booking.getPurpose());
            ps.setString(3, booking.getSource());
            ps.setString(4, booking.getDestination());
            ps.setDate(5, booking.getJourneyDate());
            ps.setTime(6, booking.getRequestedStartTime());
            ps.setTime(7, booking.getRequestedEndTime());
            ps.setInt(8, booking.getPassengerCount());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setRequestId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating booking request: " + e.getMessage());
            return false;
        }
    }

    public Booking getBookingById(int requestId) {
        String sql = "SELECT b.request_id, b.user_id, b.purpose, b.source, b.destination, b.journey_date, "
                   + "b.requested_start_time, b.requested_end_time, b.passenger_count, b.status, b.reviewed_by, "
                   + "b.reviewed_at, b.created_at, u.full_name AS requester_name, u.role AS requester_role, "
                   + "u.department, u.phone AS requester_phone, t.trip_id, v.vehicle_number, v.vehicle_type, "
                   + "d.driver_name, d.phone AS driver_phone "
                   + "FROM booking_requests b "
                   + "JOIN users u ON b.user_id = u.user_id "
                   + "LEFT JOIN trips t ON b.request_id = t.request_id "
                   + "LEFT JOIN vehicles v ON t.vehicle_id = v.vehicle_id "
                   + "LEFT JOIN drivers d ON t.driver_id = d.driver_id "
                   + "WHERE b.request_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.request_id, b.user_id, b.purpose, b.source, b.destination, b.journey_date, "
                   + "b.requested_start_time, b.requested_end_time, b.passenger_count, b.status, b.reviewed_by, "
                   + "b.reviewed_at, b.created_at, u.full_name AS requester_name, u.role AS requester_role, "
                   + "u.department, u.phone AS requester_phone, t.trip_id, v.vehicle_number, v.vehicle_type, "
                   + "d.driver_name, d.phone AS driver_phone "
                   + "FROM booking_requests b "
                   + "JOIN users u ON b.user_id = u.user_id "
                   + "LEFT JOIN trips t ON b.request_id = t.request_id "
                   + "LEFT JOIN vehicles v ON t.vehicle_id = v.vehicle_id "
                   + "LEFT JOIN drivers d ON t.driver_id = d.driver_id "
                   + "WHERE b.user_id = ? "
                   + "ORDER BY b.created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bookings for user " + userId + ": " + e.getMessage());
        }
        return list;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.request_id, b.user_id, b.purpose, b.source, b.destination, b.journey_date, "
                   + "b.requested_start_time, b.requested_end_time, b.passenger_count, b.status, b.reviewed_by, "
                   + "b.reviewed_at, b.created_at, u.full_name AS requester_name, u.role AS requester_role, "
                   + "u.department, u.phone AS requester_phone, t.trip_id, v.vehicle_number, v.vehicle_type, "
                   + "d.driver_name, d.phone AS driver_phone "
                   + "FROM booking_requests b "
                   + "JOIN users u ON b.user_id = u.user_id "
                   + "LEFT JOIN trips t ON b.request_id = t.request_id "
                   + "LEFT JOIN vehicles v ON t.vehicle_id = v.vehicle_id "
                   + "LEFT JOIN drivers d ON t.driver_id = d.driver_id "
                   + "ORDER BY b.journey_date DESC, b.requested_start_time DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all bookings: " + e.getMessage());
        }
        return list;
    }

    public List<Booking> getPendingBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.request_id, b.user_id, b.purpose, b.source, b.destination, b.journey_date, "
                   + "b.requested_start_time, b.requested_end_time, b.passenger_count, b.status, b.reviewed_by, "
                   + "b.reviewed_at, b.created_at, u.full_name AS requester_name, u.role AS requester_role, "
                   + "u.department, u.phone AS requester_phone, NULL AS trip_id, NULL AS vehicle_number, "
                   + "NULL AS vehicle_type, NULL AS driver_name, NULL AS driver_phone "
                   + "FROM booking_requests b "
                   + "JOIN users u ON b.user_id = u.user_id "
                   + "WHERE b.status = 'PENDING' "
                   + "ORDER BY b.journey_date ASC, b.requested_start_time ASC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending bookings: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(int requestId, String status, int adminUserId) {
        String sql = "UPDATE booking_requests SET status = ?, reviewed_by = ?, reviewed_at = CURRENT_TIMESTAMP "
                   + "WHERE request_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.toUpperCase());
            ps.setInt(2, adminUserId);
            ps.setInt(3, requestId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelBooking(int requestId, int userId) {
        String sql = "UPDATE booking_requests SET status = 'CANCELLED' WHERE request_id = ? AND user_id = ? AND status = 'PENDING'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Integer> getBookingStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("PENDING", 0);
        stats.put("APPROVED", 0);
        stats.put("COMPLETED", 0);
        stats.put("REJECTED", 0);
        stats.put("CANCELLED", 0);

        String sql = "SELECT status, COUNT(*) AS count FROM booking_requests GROUP BY status";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking stats: " + e.getMessage());
        }
        return stats;
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setRequestId(rs.getInt("request_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setPurpose(rs.getString("purpose"));
        b.setSource(rs.getString("source"));
        b.setDestination(rs.getString("destination"));
        b.setJourneyDate(rs.getDate("journey_date"));
        b.setRequestedStartTime(rs.getTime("requested_start_time"));
        b.setRequestedEndTime(rs.getTime("requested_end_time"));
        b.setPassengerCount(rs.getInt("passenger_count"));
        b.setStatus(rs.getString("status"));
        
        int reviewedBy = rs.getInt("reviewed_by");
        if (!rs.wasNull()) {
            b.setReviewedBy(reviewedBy);
        }
        b.setReviewedAt(rs.getTimestamp("reviewed_at"));
        b.setCreatedAt(rs.getTimestamp("created_at"));

        b.setRequesterName(rs.getString("requester_name"));
        b.setRequesterRole(rs.getString("requester_role"));
        b.setDepartment(rs.getString("department"));
        b.setRequesterPhone(rs.getString("requester_phone"));

        int tripId = rs.getInt("trip_id");
        if (!rs.wasNull()) {
            b.setTripId(tripId);
            b.setVehicleNumber(rs.getString("vehicle_number"));
            b.setVehicleType(rs.getString("vehicle_type"));
            b.setDriverName(rs.getString("driver_name"));
            b.setDriverPhone(rs.getString("driver_phone"));
        }

        return b;
    }
}
