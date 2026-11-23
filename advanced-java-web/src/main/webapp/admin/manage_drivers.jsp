<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Drivers — Admin" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <div>
            <h1 style="font-size: 1.75rem; color: #0f172a;">College Drivers Roster</h1>
            <p style="color: #64748b; font-size: 0.95rem;">Manage licensed college drivers, contact details, and duty status</p>
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

    <!-- Add Driver Card -->
    <div class="card">
        <h3 class="card-title">+ Add New Driver</h3>
        <form action="${pageContext.request.contextPath}/admin/drivers" method="post">
            <div class="form-row">
                <div class="form-group">
                    <label for="driverName">Driver Full Name *</label>
                    <input type="text" id="driverName" name="driverName" placeholder="e.g. Anand More" required>
                </div>

                <div class="form-group">
                    <label for="phone">Phone Number *</label>
                    <input type="text" id="phone" name="phone" placeholder="e.g. 9822114455" required>
                </div>

                <div class="form-group">
                    <label for="licenseNumber">Driving License Number *</label>
                    <input type="text" id="licenseNumber" name="licenseNumber" placeholder="e.g. MH12-2022-008899" required>
                </div>

                <div class="form-group">
                    <label for="status">Initial Status</label>
                    <select id="status" name="status">
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="INACTIVE">INACTIVE (On Leave)</option>
                    </select>
                </div>
            </div>

            <button type="submit" class="btn btn-secondary" style="margin-top: 0.5rem;">
                Add Driver to Roster
            </button>
        </form>
    </div>

    <!-- Drivers Table Card -->
    <div class="card">
        <h3 class="card-title">Registered Drivers Roster</h3>
        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Driver Name</th>
                        <th>Contact Phone</th>
                        <th>License Number</th>
                        <th>Duty Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="d" items="${drivers}">
                        <tr>
                            <td><strong>#${d.driverId}</strong></td>
                            <td><strong>${d.driverName}</strong></td>
                            <td>${d.phone}</td>
                            <td><code>${d.licenseNumber}</code></td>
                            <td>
                                <c:choose>
                                    <c:when test="${d.status == 'ACTIVE'}">
                                        <span class="badge badge-active">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-inactive">Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/drivers/status" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="${d.driverId}">
                                    <button type="submit" class="btn btn-sm ${d.status == 'ACTIVE' ? 'btn-danger' : 'btn-success'}">
                                        ${d.status == 'ACTIVE' ? 'Deactivate' : 'Activate'}
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
