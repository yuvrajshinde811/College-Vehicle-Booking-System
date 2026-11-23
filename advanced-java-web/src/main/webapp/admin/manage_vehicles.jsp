<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Fleet Vehicles — Admin" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <div>
            <h1 style="font-size: 1.75rem; color: #0f172a;">College Fleet Vehicles</h1>
            <p style="color: #64748b; font-size: 0.95rem;">Manage college transport fleet, capacity, and active status</p>
        </div>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">${param.error}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <!-- Add Vehicle Card -->
    <div class="card">
        <h3 class="card-title">+ Register New Vehicle</h3>
        <form action="${pageContext.request.contextPath}/admin/vehicles" method="post">
            <div class="form-row">
                <div class="form-group">
                    <label for="vehicleNumber">Registration Number *</label>
                    <input type="text" id="vehicleNumber" name="vehicleNumber" placeholder="e.g. MH-12-PQ-9999" required>
                </div>

                <div class="form-group">
                    <label for="vehicleType">Vehicle Type *</label>
                    <select id="vehicleType" name="vehicleType" required>
                        <option value="Bus (Large)">Bus (Large - 45+ seats)</option>
                        <option value="Mini Bus">Mini Bus (20-30 seats)</option>
                        <option value="Van (Tempo Traveller)">Van (Tempo Traveller - 14 seats)</option>
                        <option value="SUV (Innova)">SUV (Innova - 7 seats)</option>
                        <option value="Sedan (Dzire)">Sedan (Dzire - 4 seats)</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="capacity">Seating Capacity *</label>
                    <input type="number" id="capacity" name="capacity" min="1" max="100" placeholder="e.g. 25" required>
                </div>

                <div class="form-group">
                    <label for="status">Initial Status</label>
                    <select id="status" name="status">
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="INACTIVE">INACTIVE (Maintenance)</option>
                    </select>
                </div>
            </div>

            <button type="submit" class="btn btn-secondary" style="margin-top: 0.5rem;">
                Add Vehicle to Fleet
            </button>
        </form>
    </div>

    <!-- Vehicles Roster Table Card -->
    <div class="card">
        <h3 class="card-title">Registered Fleet Roster</h3>
        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Vehicle Number</th>
                        <th>Type / Category</th>
                        <th>Capacity</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="v" items="${vehicles}">
                        <tr>
                            <td><strong>#${v.vehicleId}</strong></td>
                            <td><strong>${v.vehicleNumber}</strong></td>
                            <td>${v.vehicleType}</td>
                            <td>${v.capacity} Passengers</td>
                            <td>
                                <c:choose>
                                    <c:when test="${v.status == 'ACTIVE'}">
                                        <span class="badge badge-active">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-inactive">Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/vehicles/status" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="${v.vehicleId}">
                                    <button type="submit" class="btn btn-sm ${v.status == 'ACTIVE' ? 'btn-danger' : 'btn-success'}">
                                        ${v.status == 'ACTIVE' ? 'Deactivate' : 'Activate'}
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
