package com.yuvraj.servlet;

import com.yuvraj.dao.DriverDAO;
import com.yuvraj.model.Driver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminDriverServlet", urlPatterns = {"/admin/drivers", "/admin/drivers/add", "/admin/drivers/status"})
public class AdminDriverServlet extends HttpServlet {

    private final DriverDAO driverDAO = new DriverDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Driver> drivers = driverDAO.getAllDrivers();
        request.setAttribute("drivers", drivers);
        request.getRequestDispatcher("/admin/manage_drivers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/admin/drivers/status".equals(path) || "toggleStatus".equals(action)) {
            handleToggleStatus(request, response);
        } else {
            handleAddDriver(request, response);
        }
    }

    private void handleAddDriver(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String driverName = request.getParameter("driverName");
        String phone = request.getParameter("phone");
        String licenseNumber = request.getParameter("licenseNumber");
        String status = request.getParameter("status");

        if (driverName == null || driverName.isBlank() || phone == null || phone.isBlank()
                || licenseNumber == null || licenseNumber.isBlank()) {
            request.setAttribute("error", "All fields are required.");
            doGet(request, response);
            return;
        }

        Driver driver = new Driver(driverName.trim(), phone.trim(), licenseNumber.trim().toUpperCase(), status != null ? status : "ACTIVE");
        boolean added = driverDAO.addDriver(driver);
        if (added) {
            response.sendRedirect(request.getContextPath() + "/admin/drivers?success=Driver registered successfully.");
        } else {
            request.setAttribute("error", "Failed to add driver. License number may already be registered.");
            doGet(request, response);
        }
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Driver d = driverDAO.getDriverById(id);
                if (d != null) {
                    d.setStatus("ACTIVE".equalsIgnoreCase(d.getStatus()) ? "INACTIVE" : "ACTIVE");
                    driverDAO.updateDriver(d);
                    response.sendRedirect(request.getContextPath() + "/admin/drivers?success=Driver status updated.");
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }
        response.sendRedirect(request.getContextPath() + "/admin/drivers?error=Could not update status.");
    }
}
