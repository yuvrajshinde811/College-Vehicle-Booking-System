<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Manage Trips — Admin" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <div>
            <h1 style="font-size: 1.75rem; color: #0f172a;">Allocated Transportation Trips</h1>
            <p style="color: #64748b; font-size: 0.95rem;">Track journey dispatch status, allocated fleet, and complete trips</p>
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
            <c:when test="${empty trips}">
                <p style="color: #64748b; padding: 1.5rem 0; text-align: center;">No trip records found.</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Trip ID</th>
                                <th>Booking Ref</th>
                                <th>Vehicle Allocated</th>
                                <th>Assigned Driver</th>
                                <th>Schedule Window</th>
                                <th>Trip Status</th>
                                <th>Remarks</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="t" items="${trips}">
                                <tr>
                                    <td><strong>#${t.tripId}</strong></td>
                                    <td>
                                        <strong>Booking #${t.requestId}</strong><br>
                                        <small style="color: #64748b;">${t.requesterName} &bull; ${t.department}</small>
                                    </td>
                                    <td>
                                        <strong>${t.vehicleNumber}</strong><br>
                                        <small style="color: #64748b;">${t.vehicleType} (${t.capacity} seats)</small>
                                    </td>
                                    <td>
                                        <strong>${t.driverName}</strong><br>
                                        <small style="color: #64748b;">Phone: ${t.driverPhone}</small>
                                    </td>
                                    <td>
                                        <strong>Start:</strong> ${t.scheduledStart}<br>
                                        <strong>End:</strong> ${t.scheduledEnd}
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.tripStatus == 'SCHEDULED'}"><span class="badge badge-approved">Scheduled</span></c:when>
                                            <c:when test="${t.tripStatus == 'IN_PROGRESS'}"><span class="badge badge-pending">In Progress</span></c:when>
                                            <c:when test="${t.tripStatus == 'COMPLETED'}"><span class="badge badge-completed">Completed</span></c:when>
                                            <c:otherwise><span class="badge badge-cancelled">${t.tripStatus}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <small>${not empty t.remarks ? t.remarks : 'N/A'}</small>
                                    </td>
                                    <td>
                                        <c:if test="${t.tripStatus == 'SCHEDULED' or t.tripStatus == 'IN_PROGRESS'}">
                                            <form action="${pageContext.request.contextPath}/admin/trips/complete" method="post" style="display: flex; gap: 0.25rem; align-items: center;">
                                                <input type="hidden" name="id" value="${t.tripId}">
                                                <input type="text" name="remarks" placeholder="Completion note" style="padding: 0.25rem 0.5rem; font-size: 0.8rem; width: 130px;">
                                                <button type="submit" class="btn btn-sm btn-success" onclick="return confirm('Mark trip #${t.tripId} as completed?');">
                                                    Complete
                                                </button>
                                            </form>
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
