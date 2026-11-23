package com.yuvraj.servlet;

import com.yuvraj.dao.BookingDAO;
import com.yuvraj.dao.DriverDAO;
import com.yuvraj.dao.TripDAO;
import com.yuvraj.dao.VehicleDAO;
import com.yuvraj.model.Booking;
import com.yuvraj.model.Trip;
import com.yuvraj.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DriverDAO driverDAO = new DriverDAO();
    private final TripDAO tripDAO = new TripDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        if (user.isAdmin()) {
            // ADMIN DASHBOARD DATA
            Map<String, Integer> bookingStats = bookingDAO.getBookingStats();
            int activeVehiclesCount = vehicleDAO.getActiveVehicles().size();
            int activeDriversCount = driverDAO.getActiveDrivers().size();
            List<Booking> pendingBookings = bookingDAO.getPendingBookings();
            List<Trip> recentTrips = tripDAO.getAllTrips();

            request.setAttribute("bookingStats", bookingStats);
            request.setAttribute("activeVehiclesCount", activeVehiclesCount);
            request.setAttribute("activeDriversCount", activeDriversCount);
            request.setAttribute("pendingBookings", pendingBookings);
            request.setAttribute("recentTrips", recentTrips);

            request.getRequestDispatcher("/admin/admin_dashboard.jsp").forward(request, response);
        } else {
            // STUDENT / STAFF USER DASHBOARD DATA
            List<Booking> userBookings = bookingDAO.getBookingsByUserId(user.getUserId());
            int total = userBookings.size();
            long pending = userBookings.stream().filter(Booking::isPending).count();
            long approved = userBookings.stream().filter(Booking::isApproved).count();
            long completed = userBookings.stream().filter(Booking::isCompleted).count();

            request.setAttribute("userBookings", userBookings);
            request.setAttribute("totalBookings", total);
            request.setAttribute("pendingCount", pending);
            request.setAttribute("approvedCount", approved);
            request.setAttribute("completedCount", completed);

            request.getRequestDispatcher("/user/user_dashboard.jsp").forward(request, response);
        }
    }
}
