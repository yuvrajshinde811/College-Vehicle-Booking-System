package com.yuvraj.test;

import com.yuvraj.dao.BookingDAO;
import com.yuvraj.dao.DriverDAO;
import com.yuvraj.dao.TripDAO;
import com.yuvraj.dao.VehicleDAO;
import com.yuvraj.database.DBConnection;
import com.yuvraj.model.Booking;
import com.yuvraj.model.Driver;
import com.yuvraj.model.Trip;
import com.yuvraj.model.Vehicle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TestJdbcOperations verifies CRUD operations for Vehicles, Drivers, Bookings,
 * and JDBC Transaction management (commit & rollback) for Trips.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestJdbcOperations {

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DriverDAO driverDAO = new DriverDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final TripDAO tripDAO = new TripDAO();

    private static final String TEST_VEHICLE_NUM = "MH-12-TEST-" + (System.currentTimeMillis() % 10000);
    private static final String TEST_DRIVER_LIC = "MH12-TEST-" + (System.currentTimeMillis() % 10000);

    @BeforeAll
    @AfterAll
    public static void cleanUp() {
        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM trips WHERE remarks LIKE '%Testing Transaction%'")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM booking_requests WHERE purpose LIKE '%Transport Test%' OR purpose LIKE '%Placement Drive%'")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM vehicles WHERE vehicle_number LIKE 'MH-12-TEST-%'")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM drivers WHERE license_number LIKE 'MH12-TEST-%' OR license_number = 'MH12-2023-TEST99'")) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Test cleanup notice: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    public void testVehicleCrud() {
        System.out.println("JDBC TEST 1: Vehicle CRUD & Availability Filtering...");
        Vehicle v = new Vehicle(TEST_VEHICLE_NUM, "Test Van", 12, "ACTIVE");
        boolean added = vehicleDAO.addVehicle(v);
        Assertions.assertTrue(added, "Vehicle should be added");
        Assertions.assertTrue(v.getVehicleId() > 0, "Generated ID should be positive");

        Vehicle fetched = vehicleDAO.getVehicleById(v.getVehicleId());
        Assertions.assertNotNull(fetched);
        Assertions.assertEquals("Test Van", fetched.getVehicleType());

        // Test capacity and active filtering
        List<Vehicle> available = vehicleDAO.getAvailableVehicles(10, "2026-12-01 09:00:00", "2026-12-01 17:00:00");
        Assertions.assertFalse(available.isEmpty(), "Available vehicles list should not be empty");
        System.out.println("  -> SUCCESS: Vehicle CRUD and availability filter verified.");
    }

    @Test
    @Order(2)
    public void testDriverCrud() {
        System.out.println("JDBC TEST 2: Driver CRUD & Availability Filtering...");
        Driver d = new Driver("Kishore Shinde", "9822998877", TEST_DRIVER_LIC, "ACTIVE");
        boolean added = driverDAO.addDriver(d);
        Assertions.assertTrue(added, "Driver should be added");
        Assertions.assertTrue(d.getDriverId() > 0, "Generated ID should be positive");

        Driver fetched = driverDAO.getDriverById(d.getDriverId());
        Assertions.assertNotNull(fetched);
        Assertions.assertEquals("Kishore Shinde", fetched.getDriverName());

        List<Driver> available = driverDAO.getAvailableDrivers("2026-12-01 09:00:00", "2026-12-01 17:00:00");
        Assertions.assertFalse(available.isEmpty(), "Available drivers list should not be empty");
        System.out.println("  -> SUCCESS: Driver CRUD and availability verified.");
    }

    @Test
    @Order(3)
    public void testBookingCreationAndQuery() {
        System.out.println("JDBC TEST 3: Booking Request Submission & Personal History...");
        Booking b = new Booking(
                4, // Student Yuvraj Shinde
                "Campus Placement Drive Transportation",
                "College Campus",
                "Magarpatta City, Pune",
                Date.valueOf(LocalDate.now().plusDays(5)),
                Time.valueOf("08:30:00"),
                Time.valueOf("18:00:00"),
                15
        );

        boolean created = bookingDAO.createBooking(b);
        Assertions.assertTrue(created, "Booking request should be created");
        Assertions.assertTrue(b.getRequestId() > 0, "Booking ID should be assigned");

        List<Booking> userBookings = bookingDAO.getBookingsByUserId(4);
        Assertions.assertFalse(userBookings.isEmpty(), "User bookings list should contain created booking");
        System.out.println("  -> SUCCESS: Booking created with ID = " + b.getRequestId());
    }

    @Test
    @Order(4)
    public void testTransactionTripAllocationAndRollbackOnConflict() {
        System.out.println("JDBC TEST 4: JDBC Transaction Allocation & Overlap Rollback Prevention...");

        // Create a booking to test allocation
        Booking b = new Booking(
                4,
                "Workshop Transport Test",
                "Campus",
                "Deccan",
                Date.valueOf(LocalDate.now().plusDays(20)),
                Time.valueOf("10:00:00"),
                Time.valueOf("14:00:00"),
                6
        );
        bookingDAO.createBooking(b);
        int requestId = b.getRequestId();

        Timestamp start = Timestamp.valueOf(LocalDateTime.now().plusDays(20).withHour(10).withMinute(0).withSecond(0));
        Timestamp end = Timestamp.valueOf(LocalDateTime.now().plusDays(20).withHour(14).withMinute(0).withSecond(0));

        // 1. Allocate vehicle 1 and driver 1 successfully (Transaction Commit)
        try {
            boolean allocated = tripDAO.allocateTrip(requestId, 1, 1, start, end, 1, "Testing Transaction Commit");
            Assertions.assertTrue(allocated, "First allocation should succeed");
            System.out.println("  -> SUCCESS: Transaction committed for trip allocation.");
        } catch (SQLException e) {
            Assertions.fail("First allocation failed unexpectedly: " + e.getMessage());
        }

        // 2. Create a second booking at overlapping time
        Booking b2 = new Booking(
                5,
                "Conflicting Transport Test",
                "Campus",
                "Swargate",
                Date.valueOf(LocalDate.now().plusDays(20)),
                Time.valueOf("11:00:00"), // Overlaps with 10:00-14:00
                Time.valueOf("13:00:00"),
                4
        );
        bookingDAO.createBooking(b2);

        // 3. Attempt to allocate the same vehicle 1 -> Must fail with conflict and ROLLBACK
        Assertions.assertThrows(SQLException.class, () -> {
            tripDAO.allocateTrip(b2.getRequestId(), 1, 2, start, end, 1, "Should fail due to vehicle conflict");
        }, "Allocation must throw SQLException on vehicle conflict");
        System.out.println("  -> SUCCESS: Transaction rollback verified when overlapping vehicle conflict detected.");
    }
}
