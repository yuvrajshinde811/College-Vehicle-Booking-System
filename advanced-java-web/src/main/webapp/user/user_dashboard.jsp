<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Dashboard — College Vehicle Booking System" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <div>
            <h1 style="font-size: 1.75rem; color: #0f172a;">Welcome, ${sessionScope.user.fullName}!</h1>
            <p style="color: #64748b; font-size: 0.95rem;">${sessionScope.user.role} &bull; ${sessionScope.user.department} Department</p>
        </div>
        <a href="${pageContext.request.contextPath}/booking/new" class="btn btn-secondary">
            + Request New Vehicle
        </a>
    </div>

    <c:if test="${not empty param.error}">
        <div class="alert alert-error">${param.error}</div>
    </c:if>

    <!-- Stats Grid -->
    <div class="stats-grid">
        <div class="stat-card">
            <span class="stat-label">Total Requests</span>
            <span class="stat-value">${totalBookings}</span>
        </div>
        <div class="stat-card amber">
            <span class="stat-label">Pending Approval</span>
            <span class="stat-value">${pendingCount}</span>
        </div>
        <div class="stat-card blue">
            <span class="stat-label">Approved &amp; Scheduled</span>
            <span class="stat-value">${approvedCount}</span>
        </div>
        <div class="stat-card green">
            <span class="stat-label">Completed Trips</span>
            <span class="stat-value">${completedCount}</span>
        </div>
    </div>

    <!-- Recent Bookings Card -->
    <div class="card">
        <div class="card-title">
            <span>Recent Booking Requests</span>
            <a href="${pageContext.request.contextPath}/booking/history" style="font-size: 0.9rem; color: #0d9488; text-decoration: none;">View All &rarr;</a>
        </div>

        <c:choose>
            <c:when test="${empty userBookings}">
                <p style="color: #64748b; padding: 1rem 0;">You have not submitted any vehicle booking requests yet.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Booking ID</th>
                                <th>Purpose</th>
                                <th>Route</th>
                                <th>Date &amp; Time</th>
                                <th>Passengers</th>
                                <th>Status</th>
                                <th>Allocated Fleet</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${userBookings}">
                                <tr>
                                    <td><strong>#${b.requestId}</strong></td>
                                    <td>${b.purpose}</td>
                                    <td>${b.source} &rarr; ${b.destination}</td>
                                    <td>${b.journeyDate}<br><small style="color: #64748b;">${b.requestedStartTime} - ${b.requestedEndTime}</small></td>
                                    <td>${b.passengerCount}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${b.status == 'PENDING'}"><span class="badge badge-pending">Pending</span></c:when>
                                            <c:when test="${b.status == 'APPROVED'}"><span class="badge badge-approved">Approved</span></c:when>
                                            <c:when test="${b.status == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                                            <c:when test="${b.status == 'REJECTED'}"><span class="badge badge-rejected">Rejected</span></c:when>
                                            <c:otherwise><span class="badge badge-cancelled">${b.status}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty b.vehicleNumber}">
                                                <strong>${b.vehicleNumber}</strong> (${b.vehicleType})<br>
                                                <small style="color: #64748b;">Driver: ${b.driverName} (${b.driverPhone})</small>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #94a3b8; font-style: italic;">Awaiting Allocation</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
