<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Login — College Vehicle Booking System" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container" style="max-width: 480px; margin-top: 3rem;">
    <div class="card">
        <h2 class="card-title" style="justify-content: center; font-size: 1.5rem; color: #1e3a8a;">
            Sign In to Portal
        </h2>

        <c:if test="${not empty param.registered}">
            <div class="alert alert-success">
                Registration successful! Please sign in with your credentials.
            </div>
        </c:if>

        <c:if test="${not empty param.loggedOut}">
            <div class="alert alert-success">
                You have been safely logged out.
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>

        <c:if test="${not empty param.error}">
            <div class="alert alert-error">
                ${param.error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label for="email">College Email Address</label>
                <input type="email" id="email" name="email" value="${email != null ? email : ''}" placeholder="e.g. name@college.edu" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="Enter your password" required>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem; font-size: 1rem;">
                Sign In
            </button>
        </form>

        <div style="text-align: center; margin-top: 1.25rem; font-size: 0.9rem; color: #64748b;">
            Don't have an account? <a href="${pageContext.request.contextPath}/register" style="color: #0d9488; font-weight: 600; text-decoration: none;">Register here</a>
        </div>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
