<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Allocate Vehicle & Driver — Admin" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container" style="max-width: 720px;">
    <div class="card">
        <h2 class="card-title">
            <span>&#128652; Allocate Fleet &amp; Driver for Booking #${booking.requestId}</span>
            <a href="${pageContext.request.contextPath}/admin/bookings" style="font-size: 0.85rem; color: #64748b; text-decoration: none;">&larr; Back to List</a>
        </h2>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <!-- Booking Summary Box -->
        <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 1.25rem; margin-bottom: 1.5rem; font-size: 0.95rem;">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem;">
                <div><strong>Requester:</strong> ${booking.requesterName} (${booking.requesterRole})</div>
                <div><strong>Department:</strong> ${booking.department}</div>
                <div><strong>Route:</strong> ${booking.source} &rarr; ${booking.destination}</div>
                <div><strong>Passenger Count:</strong> <span class="badge badge-approved">${booking.passengerCount} Passengers</span></div>
                <div><strong>Journey Date:</strong> ${booking.journeyDate}</div>
                <div><strong>Schedule:</strong> ${booking.requestedStartTime} &ndash; ${booking.requestedEndTime}</div>
                <div style="grid-column: 1 / -1;"><strong>Purpose:</strong> ${booking.purpose}</div>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/admin/bookings/allocate" method="post">
            <input type="hidden" name="requestId" value="${booking.requestId}">

            <!-- Select Vehicle -->
            <div class="form-group">
                <label for="vehicleId">
                    Assign Available Vehicle * (Filtered by capacity &ge; ${booking.passengerCount} and schedule)
                </label>
                <c:choose>
                    <c:when test="${empty availableVehicles}">
                        <div class="alert alert-error" style="margin-top: 0.5rem;">
                            No active vehicles with capacity &ge; ${booking.passengerCount} are available during this time slot.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <select id="vehicleId" name="vehicleId" required>
                            <option value="">-- Choose an available vehicle --</option>
                            <c:forEach var="v" items="${availableVehicles}">
                                <option value="${v.vehicleId}">
                                    ${v.vehicleNumber} &mdash; ${v.vehicleType} (Capacity: ${v.capacity} seats)
                                </option>
                            </c:forEach>
                        </select>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Select Driver -->
            <div class="form-group">
                <label for="driverId">
                    Assign Available Driver * (Filtered by active status and schedule availability)
                </label>
                <c:choose>
                    <c:when test="${empty availableDrivers}">
                        <div class="alert alert-error" style="margin-top: 0.5rem;">
                            No active drivers are available for this journey time slot.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <select id="driverId" name="driverId" required>
                            <option value="">-- Choose an available driver --</option>
                            <c:forEach var="d" items="${availableDrivers}">
                                <option value="${d.driverId}">
                                    ${d.driverName} (Phone: ${d.phone}, Lic: ${d.licenseNumber})
                                </option>
                            </c:forEach>
                        </select>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Allocation Remarks -->
            <div class="form-group">
                <label for="remarks">Admin Remarks / Instructions for Driver</label>
                <input type="text" id="remarks" name="remarks" placeholder="e.g. Approved by Principal. Report at Main Gate 15 minutes prior.">
            </div>

            <div style="display: flex; gap: 1rem; margin-top: 1.5rem;">
                <button type="submit" class="btn btn-success" style="flex: 1; padding: 0.75rem;" 
                        <c:if test="${empty availableVehicles or empty availableDrivers}">disabled</c:if>>
                    Confirm Allocation &amp; Schedule Trip
                </button>
                <a href="${pageContext.request.contextPath}/admin/bookings" class="btn" style="background: #e2e8f0; color: #334155;">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
