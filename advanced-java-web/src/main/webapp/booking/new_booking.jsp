<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="New Vehicle Request — College Vehicle Booking System" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container" style="max-width: 680px;">
    <div class="card">
        <h2 class="card-title">
            <span>&#128652; Vehicle Booking Request</span>
            <span style="font-size: 0.85rem; font-weight: normal; color: #64748b;">Step 1 of 2: Submission</span>
        </h2>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/booking" method="post">
            <div class="form-group">
                <label for="purpose">Purpose of Travel *</label>
                <input type="text" id="purpose" name="purpose" placeholder="e.g. Industrial Visit to Tech Park / University Sports Tournament" required autofocus>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="source">Pickup Location *</label>
                    <input type="text" id="source" name="source" placeholder="e.g. College Main Gate / Campus Admin Block" required>
                </div>

                <div class="form-group">
                    <label for="destination">Destination *</label>
                    <input type="text" id="destination" name="destination" placeholder="e.g. Hinjawadi Phase 1, Pune" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="journeyDate">Journey Date *</label>
                    <input type="date" id="journeyDate" name="journeyDate" required>
                </div>

                <div class="form-group">
                    <label for="passengerCount">Number of Passengers *</label>
                    <input type="number" id="passengerCount" name="passengerCount" min="1" max="100" value="1" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="startTime">Requested Start Time *</label>
                    <input type="time" id="startTime" name="startTime" required>
                </div>

                <div class="form-group">
                    <label for="endTime">Requested Return / End Time *</label>
                    <input type="time" id="endTime" name="endTime" required>
                </div>
            </div>

            <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                <button type="submit" class="btn btn-primary" style="flex: 1; padding: 0.75rem;">
                    Submit Booking Request
                </button>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn" style="background: #e2e8f0; color: #334155;">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
