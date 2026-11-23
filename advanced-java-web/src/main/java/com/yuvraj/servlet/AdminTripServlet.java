package com.yuvraj.servlet;

import com.yuvraj.dao.TripDAO;
import com.yuvraj.model.Trip;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AdminTripServlet", urlPatterns = {"/admin/trips", "/admin/trips/complete"})
public class AdminTripServlet extends HttpServlet {

    private final TripDAO tripDAO = new TripDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Trip> trips = tripDAO.getAllTrips();
        request.setAttribute("trips", trips);
        request.getRequestDispatcher("/admin/manage_trips.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/admin/trips/complete".equals(path) || "complete".equals(action)) {
            handleCompleteTrip(request, response);
        } else {
            doGet(request, response);
        }
    }

    private void handleCompleteTrip(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        String remarks = request.getParameter("remarks");

        if (idStr != null) {
            try {
                int tripId = Integer.parseInt(idStr);
                boolean completed = tripDAO.completeTrip(tripId, remarks);
                if (completed) {
                    response.sendRedirect(request.getContextPath() + "/admin/trips?success=Trip #" + tripId + " marked as COMPLETED.");
                    return;
                }
            } catch (SQLException e) {
                response.sendRedirect(request.getContextPath() + "/admin/trips?error=Database error: " + e.getMessage());
                return;
            } catch (NumberFormatException ignored) {}
        }
        response.sendRedirect(request.getContextPath() + "/admin/trips?error=Could not complete trip.");
    }
}
