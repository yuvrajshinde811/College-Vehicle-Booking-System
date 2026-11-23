package com.yuvraj.servlet;

import com.yuvraj.dao.BookingDAO;
import com.yuvraj.dao.DriverDAO;
import com.yuvraj.dao.TripDAO;
import com.yuvraj.dao.VehicleDAO;
import com.yuvraj.model.Booking;
import com.yuvraj.model.Driver;
import com.yuvraj.model.User;
import com.yuvraj.model.Vehicle;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet(name = "AdminBookingServlet", urlPatterns = {"/admin/bookings", "/admin/bookings/allocate", "/admin/bookings/reject"})
public class AdminBookingServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DriverDAO driverDAO = new DriverDAO();
    private final TripDAO tripDAO = new TripDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/admin/bookings/allocate".equals(path) || "allocate".equals(action)) {
            showAllocationPage(request, response);
        } else {
            // Default: List all bookings with pending filter option
            String filter = request.getParameter("filter");
            List<Booking> bookings;
            if ("pending".equalsIgnoreCase(filter)) {
                bookings = bookingDAO.getPendingBookings();
            } else {
                bookings = bookingDAO.getAllBookings();
            }
            request.setAttribute("bookings", bookings);
            request.setAttribute("filter", filter);
            request.getRequestDispatcher("/admin/manage_bookings.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User adminUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (adminUser == null || !adminUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/admin/bookings/reject".equals(path) || "reject".equals(action)) {
            handleReject(request, response, adminUser);
        } else if ("/admin/bookings/allocate".equals(path) || "allocate".equals(action)) {
            handleAllocate(request, response, adminUser);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }

    private void showAllocationPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
            return;
        }

        try {
            int requestId = Integer.parseInt(idStr);
            Booking booking = bookingDAO.getBookingById(requestId);
            if (booking == null) {
                response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Booking not found");
                return;
            }

            // Build start and end timestamps string for availability check
            String startDateTime = booking.getJourneyDate() + " " + booking.getRequestedStartTime();
            String endDateTime = booking.getJourneyDate() + " " + booking.getRequestedEndTime();

            List<Vehicle> availableVehicles = vehicleDAO.getAvailableVehicles(booking.getPassengerCount(), startDateTime, endDateTime);
            List<Driver> availableDrivers = driverDAO.getAvailableDrivers(startDateTime, endDateTime);

            request.setAttribute("booking", booking);
            request.setAttribute("availableVehicles", availableVehicles);
            request.setAttribute("availableDrivers", availableDrivers);
            request.getRequestDispatcher("/admin/allocate_trip.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Invalid booking ID");
        }
    }

    private void handleReject(HttpServletRequest request, HttpServletResponse response, User adminUser)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int requestId = Integer.parseInt(idStr);
                boolean rejected = bookingDAO.updateStatus(requestId, "REJECTED", adminUser.getUserId());
                if (rejected) {
                    response.sendRedirect(request.getContextPath() + "/admin/bookings?success=Booking #" + requestId + " rejected.");
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }
        response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Could not reject booking.");
    }

    private void handleAllocate(HttpServletRequest request, HttpServletResponse response, User adminUser)
            throws ServletException, IOException {

        String requestIdStr = request.getParameter("requestId");
        String vehicleIdStr = request.getParameter("vehicleId");
        String driverIdStr = request.getParameter("driverId");
        String remarks = request.getParameter("remarks");

        if (requestIdStr == null || vehicleIdStr == null || driverIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Missing allocation parameters");
            return;
        }

        try {
            int requestId = Integer.parseInt(requestIdStr);
            int vehicleId = Integer.parseInt(vehicleIdStr);
            int driverId = Integer.parseInt(driverIdStr);

            Booking booking = bookingDAO.getBookingById(requestId);
            if (booking == null) {
                response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Booking not found");
                return;
            }

            Timestamp scheduledStart = Timestamp.valueOf(booking.getJourneyDate() + " " + booking.getRequestedStartTime());
            Timestamp scheduledEnd = Timestamp.valueOf(booking.getJourneyDate() + " " + booking.getRequestedEndTime());

            // Allocate via JDBC Transaction in TripDAO
            tripDAO.allocateTrip(requestId, vehicleId, driverId, scheduledStart, scheduledEnd, adminUser.getUserId(), remarks);

            response.sendRedirect(request.getContextPath() + "/admin/trips?success=Vehicle and Driver successfully allocated for Booking #" + requestId + "!");

        } catch (SQLException e) {
            request.setAttribute("error", "Allocation failed: " + e.getMessage());
            showAllocationPage(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Invalid allocation input: " + e.getMessage());
        }
    }
}
