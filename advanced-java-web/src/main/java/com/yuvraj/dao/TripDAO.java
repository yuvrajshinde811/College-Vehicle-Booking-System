package com.yuvraj.dao;

import com.yuvraj.database.DBConnection;
import com.yuvraj.model.Trip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * TripDAO handles Trip management and demonstrates JDBC Transaction Management
 * (commit, rollback, setAutoCommit) for atomic allocation and completion.
 */
public class TripDAO {

    /**
     * Demonstrates JDBC TRANSACTION:
     * Allocates a vehicle and driver to a booking request atomically:
     * 1. Checks that vehicle is not booked in an overlapping time window.
     * 2. Checks that driver is not assigned in an overlapping time window.
     * 3. Inserts the trip record.
     * 4. Updates booking status to 'APPROVED' with admin reviewer info.
     * If any step fails, the transaction is rolled back.
     */
    public boolean allocateTrip(int requestId, int vehicleId, int driverId,
                                Timestamp scheduledStart, Timestamp scheduledEnd,
                                int adminUserId, String remarks) throws SQLException {

        String checkVehicleOverlapSql =
                "SELECT COUNT(*) FROM trips WHERE vehicle_id = ? "
              + "AND trip_status IN ('SCHEDULED', 'IN_PROGRESS') "
              + "AND (scheduled_start < ? AND scheduled_end > ?)";

        String checkDriverOverlapSql =
                "SELECT COUNT(*) FROM trips WHERE driver_id = ? "
              + "AND trip_status IN ('SCHEDULED', 'IN_PROGRESS') "
              + "AND (scheduled_start < ? AND scheduled_end > ?)";

        String insertTripSql =
                "INSERT INTO trips (request_id, vehicle_id, driver_id, scheduled_start, scheduled_end, trip_status, remarks) "
              + "VALUES (?, ?, ?, ?, ?, 'SCHEDULED', ?)";

        String updateBookingSql =
                "UPDATE booking_requests SET status = 'APPROVED', reviewed_by = ?, reviewed_at = CURRENT_TIMESTAMP "
              + "WHERE request_id = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // START TRANSACTION

            // 1. Check vehicle conflict
            try (PreparedStatement psCheckV = con.prepareStatement(checkVehicleOverlapSql)) {
                psCheckV.setInt(1, vehicleId);
                psCheckV.setTimestamp(2, scheduledEnd);
                psCheckV.setTimestamp(3, scheduledStart);
                try (ResultSet rs = psCheckV.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        con.rollback();
                        throw new SQLException("Selected vehicle is already allocated to another trip during this time window.");
                    }
                }
            }

            // 2. Check driver conflict
            try (PreparedStatement psCheckD = con.prepareStatement(checkDriverOverlapSql)) {
                psCheckD.setInt(1, driverId);
                psCheckD.setTimestamp(2, scheduledEnd);
                psCheckD.setTimestamp(3, scheduledStart);
                try (ResultSet rs = psCheckD.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        con.rollback();
                        throw new SQLException("Selected driver is already assigned to another trip during this time window.");
                    }
                }
            }

            // 3. Insert trip record
            try (PreparedStatement psTrip = con.prepareStatement(insertTripSql)) {
                psTrip.setInt(1, requestId);
                psTrip.setInt(2, vehicleId);
                psTrip.setInt(3, driverId);
                psTrip.setTimestamp(4, scheduledStart);
                psTrip.setTimestamp(5, scheduledEnd);
                psTrip.setString(6, remarks);
                psTrip.executeUpdate();
            }

            // 4. Update booking status to APPROVED
            try (PreparedStatement psBooking = con.prepareStatement(updateBookingSql)) {
                psBooking.setInt(1, adminUserId);
                psBooking.setInt(2, requestId);
                psBooking.executeUpdate();
            }

            con.commit(); // COMMIT TRANSACTION
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // ROLLBACK ON FAILURE
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    System.err.println("Closing connection failed: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Demonstrates JDBC TRANSACTION:
     * Marks a trip as COMPLETED and updates both Trip and Booking records atomically.
     */
    public boolean completeTrip(int tripId, String remarks) throws SQLException {
        String updateTripSql = "UPDATE trips SET trip_status = 'COMPLETED', actual_end = CURRENT_TIMESTAMP, "
                             + "remarks = COALESCE(?, remarks) WHERE trip_id = ?";

        String updateBookingSql = "UPDATE booking_requests SET status = 'COMPLETED' "
                                + "WHERE request_id = (SELECT request_id FROM trips WHERE trip_id = ?)";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // START TRANSACTION

            try (PreparedStatement psTrip = con.prepareStatement(updateTripSql)) {
                psTrip.setString(1, remarks);
                psTrip.setInt(2, tripId);
                psTrip.executeUpdate();
            }

            try (PreparedStatement psBooking = con.prepareStatement(updateBookingSql)) {
                psBooking.setInt(1, tripId);
                psBooking.executeUpdate();
            }

            con.commit(); // COMMIT TRANSACTION
            return true;
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    System.err.println("Closing connection failed: " + ex.getMessage());
                }
            }
        }
    }

    public Trip getTripById(int tripId) {
        String sql = "SELECT t.trip_id, t.request_id, t.vehicle_id, t.driver_id, t.scheduled_start, t.scheduled_end, "
                   + "t.actual_start, t.actual_end, t.trip_status, t.remarks, t.created_at, "
                   + "u.full_name AS requester_name, u.department, b.purpose, b.source, b.destination, "
                   + "v.vehicle_number, v.vehicle_type, v.capacity, d.driver_name, d.phone AS driver_phone "
                   + "FROM trips t "
                   + "JOIN booking_requests b ON t.request_id = b.request_id "
                   + "JOIN users u ON b.user_id = u.user_id "
                   + "JOIN vehicles v ON t.vehicle_id = v.vehicle_id "
                   + "JOIN drivers d ON t.driver_id = d.driver_id "
                   + "WHERE t.trip_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrip(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching trip by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Trip> getAllTrips() {
        List<Trip> list = new ArrayList<>();
        String sql = "SELECT t.trip_id, t.request_id, t.vehicle_id, t.driver_id, t.scheduled_start, t.scheduled_end, "
                   + "t.actual_start, t.actual_end, t.trip_status, t.remarks, t.created_at, "
                   + "u.full_name AS requester_name, u.department, b.purpose, b.source, b.destination, "
                   + "v.vehicle_number, v.vehicle_type, v.capacity, d.driver_name, d.phone AS driver_phone "
                   + "FROM trips t "
                   + "JOIN booking_requests b ON t.request_id = b.request_id "
                   + "JOIN users u ON b.user_id = u.user_id "
                   + "JOIN vehicles v ON t.vehicle_id = v.vehicle_id "
                   + "JOIN drivers d ON t.driver_id = d.driver_id "
                   + "ORDER BY t.scheduled_start DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToTrip(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all trips: " + e.getMessage());
        }
        return list;
    }

    private Trip mapResultSetToTrip(ResultSet rs) throws SQLException {
        Trip t = new Trip();
        t.setTripId(rs.getInt("trip_id"));
        t.setRequestId(rs.getInt("request_id"));
        t.setVehicleId(rs.getInt("vehicle_id"));
        t.setDriverId(rs.getInt("driver_id"));
        t.setScheduledStart(rs.getTimestamp("scheduled_start"));
        t.setScheduledEnd(rs.getTimestamp("scheduled_end"));
        t.setActualStart(rs.getTimestamp("actual_start"));
        t.setActualEnd(rs.getTimestamp("actual_end"));
        t.setTripStatus(rs.getString("trip_status"));
        t.setRemarks(rs.getString("remarks"));
        t.setCreatedAt(rs.getTimestamp("created_at"));

        t.setRequesterName(rs.getString("requester_name"));
        t.setDepartment(rs.getString("department"));
        t.setPurpose(rs.getString("purpose"));
        t.setSource(rs.getString("source"));
        t.setDestination(rs.getString("destination"));

        t.setVehicleNumber(rs.getString("vehicle_number"));
        t.setVehicleType(rs.getString("vehicle_type"));
        t.setCapacity(rs.getInt("capacity"));

        t.setDriverName(rs.getString("driver_name"));
        t.setDriverPhone(rs.getString("driver_phone"));

        return t;
    }
}
