<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="My Bookings — College Vehicle Booking System" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <h1 style="font-size: 1.75rem; color: #0f172a;">My Booking History</h1>
        <a href="${pageContext.request.contextPath}/booking/new" class="btn btn-secondary">
            + New Request
        </a>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">${param.error}</div>
    </c:if>

    <div class="card">
        <c:choose>
            <c:when test="${empty bookings}">
                <p style="color: #64748b; padding: 1.5rem 0; text-align: center;">No booking records found.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Purpose</th>
                                <th>Route</th>
                                <th>Journey Schedule</th>
                                <th>Passengers</th>
                                <th>Status</th>
                                <th>Allocated Vehicle &amp; Driver</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${bookings}">
                                <tr>
                                    <td><strong>#${b.requestId}</strong></td>
                                    <td>${b.purpose}</td>
                                    <td>${b.source} &rarr; ${b.destination}</td>
                                    <td>
                                        ${b.journeyDate}<br>
                                        <small style="color: #64748b;">${b.requestedStartTime} &ndash; ${b.requestedEndTime}</small>
                                    </td>
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
                                                <span style="color: #94a3b8; font-style: italic;">Pending Allocation</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${b.status == 'PENDING'}">
                                            <a href="${pageContext.request.contextPath}/booking/cancel?id=${b.requestId}" 
                                               class="btn btn-sm btn-danger"
                                               onclick="return confirm('Are you sure you want to cancel this booking request?');">
                                                Cancel
                                            </a>
                                        </c:if>
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
