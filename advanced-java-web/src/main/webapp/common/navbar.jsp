<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="brand">
        &#128652; College Vehicle Booking
    </a>

    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/">Home</a></li>

        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <li><a href="${pageContext.request.contextPath}/login">Login</a></li>
                <li><a href="${pageContext.request.contextPath}/register">Register</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>

                <c:choose>
                    <c:when test="${sessionScope.user.role == 'ADMIN'}">
                        <li><a href="${pageContext.request.contextPath}/admin/bookings">Manage Bookings</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/vehicles">Vehicles</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/drivers">Drivers</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/trips">Trips</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/booking/new">New Booking</a></li>
                        <li><a href="${pageContext.request.contextPath}/booking/history">My Bookings</a></li>
                    </c:otherwise>
                </c:choose>

                <li>
                    <span class="user-badge">
                        &#128100; ${sessionScope.user.fullName} (${sessionScope.user.role})
                    </span>
                </li>
                <li><a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-danger">Logout</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</nav>
