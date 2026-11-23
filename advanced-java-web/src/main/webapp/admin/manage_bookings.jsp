<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Bookings — Admin" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <div>
            <h1 style="font-size: 1.75rem; color: #0f172a;">All Booking Requests</h1>
            <p style="color: #64748b; font-size: 0.95rem;">Review, approve, or reject student and faculty transportation requests</p>
        </div>
        <div style="display: flex; gap: 0.5rem;">
            <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-sm ${empty filter ? 'btn-primary' : 'btn-secondary'}">
                All Bookings
            </a>
            <a href="${pageContext.request.contextPath}/admin/bookings?filter=pending" class="btn btn-sm ${filter == 'pending' ? 'btn-primary' : 'btn-secondary'}">
                Pending Only
            </a>
        </div>
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
                <p style="color: #64748b; padding: 1.5rem 0; text-align: center;">No booking requests found.</p>
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
                                <th>Date &amp; Schedule</th>
                                <th>Seats</th>
                                <th>Status</th>
                                <th>Allocation</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${bookings}">
                                <tr>
                                    <td><strong>#${b.requestId}</strong></td>
                                    <td>
                                        <strong>${b.requesterName}</strong><br>
                                        <small style="color: #64748b;">${b.requesterRole} &bull; ${b.department}</small>
                                    </td>
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
                                                <strong>${b.vehicleNumber}</strong><br>
                                                <small style="color: #64748b;">${b.driverName}</small>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #94a3b8; font-style: italic;">None</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${b.status == 'PENDING'}">
                                            <div style="display: flex; gap: 0.35rem;">
                                                <a href="${pageContext.request.contextPath}/admin/bookings/allocate?id=${b.requestId}" class="btn btn-sm btn-success">
                                                    Allocate
                                                </a>
                                                <form action="${pageContext.request.contextPath}/admin/bookings/reject" method="post" onsubmit="return confirm('Reject booking #${b.requestId}?');">
                                                    <input type="hidden" name="id" value="${b.requestId}">
                                                    <button type="submit" class="btn btn-sm btn-danger">Reject</button>
                                                </form>
                                            </div>
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
