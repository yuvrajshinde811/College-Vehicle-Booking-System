package com.yuvraj.servlet;

import com.yuvraj.dao.BookingDAO;
import com.yuvraj.model.Booking;
import com.yuvraj.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "BookingServlet", urlPatterns = {"/booking", "/booking/new", "/booking/history", "/booking/cancel"})
public class BookingServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/booking/new".equals(path) || "new".equals(action)) {
            request.getRequestDispatcher("/booking/new_booking.jsp").forward(request, response);
        } else if ("/booking/cancel".equals(path) || "cancel".equals(action)) {
            handleCancel(request, response, user);
        } else {
            // Default to personal booking history
            List<Booking> bookings = bookingDAO.getBookingsByUserId(user.getUserId());
            request.setAttribute("bookings", bookings);
            request.getRequestDispatcher("/booking/my_bookings.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String purpose = request.getParameter("purpose");
        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        String journeyDateStr = request.getParameter("journeyDate");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String passengerCountStr = request.getParameter("passengerCount");

        // Server-side validation
        if (purpose == null || purpose.isBlank() || source == null || source.isBlank()
                || destination == null || destination.isBlank() || journeyDateStr == null || journeyDateStr.isBlank()
                || startTimeStr == null || startTimeStr.isBlank() || endTimeStr == null || endTimeStr.isBlank()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/booking/new_booking.jsp").forward(request, response);
            return;
        }

        try {
            Date journeyDate = Date.valueOf(journeyDateStr);
            if (journeyDate.toLocalDate().isBefore(LocalDate.now())) {
                request.setAttribute("error", "Journey date cannot be in the past.");
                request.getRequestDispatcher("/booking/new_booking.jsp").forward(request, response);
                return;
            }

            // Append seconds if submitted as HH:mm
            if (startTimeStr.length() == 5) startTimeStr += ":00";
            if (endTimeStr.length() == 5) endTimeStr += ":00";

            Time startTime = Time.valueOf(startTimeStr);
            Time endTime = Time.valueOf(endTimeStr);

            if (!endTime.after(startTime)) {
                request.setAttribute("error", "Requested end time must be after start time.");
                request.getRequestDispatcher("/booking/new_booking.jsp").forward(request, response);
                return;
            }

            int passengerCount = Integer.parseInt(passengerCountStr);
            if (passengerCount <= 0) {
                request.setAttribute("error", "Passenger count must be at least 1.");
                request.getRequestDispatcher("/booking/new_booking.jsp").forward(request, response);
                return;
            }

            Booking booking = new Booking(
                    user.getUserId(),
                    purpose.trim(),
                    source.trim(),
                    destination.trim(),
                    journeyDate,
                    startTime,
                    endTime,
                    passengerCount
            );

            boolean success = bookingDAO.createBooking(booking);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/booking/history?success=Booking request submitted successfully! Status: PENDING");
            } else {
                request.setAttribute("error", "Failed to submit booking request. Please check inputs.");
                request.getRequestDispatcher("/booking/new_booking.jsp").forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date, time, or passenger count format.");
            request.getRequestDispatcher("/booking/new_booking.jsp").forward(request, response);
        }
    }

    private void handleCancel(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int requestId = Integer.parseInt(idStr);
                boolean cancelled = bookingDAO.cancelBooking(requestId, user.getUserId());
                if (cancelled) {
                    response.sendRedirect(request.getContextPath() + "/booking/history?success=Booking request cancelled successfully.");
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }
        response.sendRedirect(request.getContextPath() + "/booking/history?error=Could not cancel booking.");
    }
}
