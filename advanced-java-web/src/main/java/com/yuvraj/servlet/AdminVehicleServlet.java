package com.yuvraj.servlet;

import com.yuvraj.dao.VehicleDAO;
import com.yuvraj.model.Vehicle;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminVehicleServlet", urlPatterns = {"/admin/vehicles", "/admin/vehicles/add", "/admin/vehicles/status"})
public class AdminVehicleServlet extends HttpServlet {

    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
        request.setAttribute("vehicles", vehicles);
        request.getRequestDispatcher("/admin/manage_vehicles.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        if ("/admin/vehicles/status".equals(path) || "toggleStatus".equals(action)) {
            handleToggleStatus(request, response);
        } else {
            handleAddVehicle(request, response);
        }
    }

    private void handleAddVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String vehicleNumber = request.getParameter("vehicleNumber");
        String vehicleType = request.getParameter("vehicleType");
        String capacityStr = request.getParameter("capacity");
        String status = request.getParameter("status");

        if (vehicleNumber == null || vehicleNumber.isBlank() || vehicleType == null || vehicleType.isBlank()
                || capacityStr == null || capacityStr.isBlank()) {
            request.setAttribute("error", "All fields are required.");
            doGet(request, response);
            return;
        }

        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                request.setAttribute("error", "Capacity must be greater than 0.");
                doGet(request, response);
                return;
            }

            Vehicle vehicle = new Vehicle(vehicleNumber.trim().toUpperCase(), vehicleType.trim(), capacity, status != null ? status : "ACTIVE");
            boolean added = vehicleDAO.addVehicle(vehicle);
            if (added) {
                response.sendRedirect(request.getContextPath() + "/admin/vehicles?success=Vehicle added successfully.");
            } else {
                request.setAttribute("error", "Failed to add vehicle. Number may already be registered.");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid capacity format.");
            doGet(request, response);
        }
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Vehicle v = vehicleDAO.getVehicleById(id);
                if (v != null) {
                    v.setStatus("ACTIVE".equalsIgnoreCase(v.getStatus()) ? "INACTIVE" : "ACTIVE");
                    vehicleDAO.updateVehicle(v);
                    response.sendRedirect(request.getContextPath() + "/admin/vehicles?success=Vehicle status updated.");
                    return;
                }
            } catch (NumberFormatException ignored) {}
        }
        response.sendRedirect(request.getContextPath() + "/admin/vehicles?error=Could not update status.");
    }
}
