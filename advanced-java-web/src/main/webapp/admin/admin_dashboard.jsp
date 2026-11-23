<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Admin Dashboard — College Vehicle Booking System" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <div>
            <h1 style="font-size: 1.75rem; color: #0f172a;">Administrator Command Center</h1>
            <p style="color: #64748b; font-size: 0.95rem;">College Fleet Management &amp; Transportation Logistics</p>
        </div>
        <div style="display: flex; gap: 0.5rem;">
            <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-primary btn-sm">Manage Bookings</a>
            <a href="${pageContext.request.contextPath}/admin/vehicles" class="btn btn-secondary btn-sm">Vehicles</a>
            <a href="${pageContext.request.contextPath}/admin/drivers" class="btn btn-secondary btn-sm">Drivers</a>
            <a href="${pageContext.request.contextPath}/admin/trips" class="btn btn-secondary btn-sm">Trips</a>
        </div>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">${param.error}</div>
    </c:if>

    <!-- Stats Grid -->
    <div class="stats-grid">
        <div class="stat-card amber">
            <span class="stat-label">Pending Requests</span>
            <span class="stat-value">${bookingStats.PENDING != null ? bookingStats.PENDING : 0}</span>
        </div>
        <div class="stat-card blue">
            <span class="stat-label">Approved Bookings</span>
            <span class="stat-value">${bookingStats.APPROVED != null ? bookingStats.APPROVED : 0}</span>
        </div>
        <div class="stat-card green">
            <span class="stat-label">Completed Trips</span>
            <span class="stat-value">${bookingStats.COMPLETED != null ? bookingStats.COMPLETED : 0}</span>
        </div>
        <div class="stat-card">
            <span class="stat-label">Active Fleet Vehicles</span>
            <span class="stat-value">${activeVehiclesCount}</span>
        </div>
        <div class="stat-card teal">
            <span class="stat-label">Active Drivers</span>
            <span class="stat-value">${activeDriversCount}</span>
        </div>
    </div>

    <!-- Pending Requests Action Card -->
    <div class="card">
        <div class="card-title">
            <span>&#9888;&#65039; Pending Booking Requests Awaiting Approval</span>
            <a href="${pageContext.request.contextPath}/admin/bookings?filter=pending" style="font-size: 0.9rem; color: #0d9488; text-decoration: none;">View All Pending &rarr;</a>
        </div>

        <c:choose>
            <c:when test="${empty pendingBookings}">
                <p style="color: #64748b; padding: 1rem 0;">No pending booking requests at this time. All caught up!</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Requester</th>
                                <th>Purpose</th>
                                <th>Route</th>
                                <th>Date &amp; Time</th>
                                <th>Passengers</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${pendingBookings}">
                                <tr>
                                    <td><strong>#${b.requestId}</strong></td>
                                    <td>${b.requesterName}<br><small style="color: #64748b;">${b.requesterRole} &bull; ${b.department}</small></td>
                                    <td>${b.purpose}</td>
                                    <td>${b.source} &rarr; ${b.destination}</td>
                                    <td>${b.journeyDate}<br><small style="color: #64748b;">${b.requestedStartTime} &ndash; ${b.requestedEndTime}</small></td>
                                    <td><strong>${b.passengerCount}</strong></td>
                                    <td>
                                        <div style="display: flex; gap: 0.5rem;">
                                            <a href="${pageContext.request.contextPath}/admin/bookings/allocate?id=${b.requestId}" class="btn btn-sm btn-success">
                                                Approve &amp; Allocate
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/bookings/reject" method="post" style="display: inline;" onsubmit="return confirm('Reject this booking request?');">
                                                <input type="hidden" name="id" value="${b.requestId}">
                                                <button type="submit" class="btn btn-sm btn-danger">Reject</button>
                                            </form>
                                        </div>
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
