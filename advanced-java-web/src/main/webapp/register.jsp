<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Register — College Vehicle Booking System" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container" style="max-width: 560px; margin-top: 2rem;">
    <div class="card">
        <h2 class="card-title" style="justify-content: center; font-size: 1.5rem; color: #1e3a8a;">
            Create Your Account
        </h2>

        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group">
                <label for="fullName">Full Name</label>
                <input type="text" id="fullName" name="fullName" value="${fullName != null ? fullName : ''}" placeholder="Enter your full name" required autofocus>
            </div>

            <div class="form-group">
                <label for="email">College Email Address</label>
                <input type="email" id="email" name="email" placeholder="e.g. yourname@college.edu" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="role">Register As</label>
                    <select id="role" name="role" required>
                        <option value="STUDENT">Student</option>
                        <option value="STAFF">Staff / Faculty</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="department">Department</label>
                    <input type="text" id="department" name="department" value="${department != null ? department : ''}" placeholder="e.g. MCA, MBA, CS" required>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="text" id="phone" name="phone" value="${phone != null ? phone : ''}" placeholder="e.g. 9876543210" required>
                </div>

                <div class="form-group">
                    <label for="password">Password (min 6 characters)</label>
                    <input type="password" id="password" name="password" placeholder="Create a strong password" minlength="6" required>
                </div>
            </div>

            <button type="submit" class="btn btn-secondary" style="width: 100%; padding: 0.75rem; font-size: 1rem; margin-top: 0.5rem;">
                Complete Registration
            </button>
        </form>

        <div style="text-align: center; margin-top: 1.25rem; font-size: 0.9rem; color: #64748b;">
            Already have an account? <a href="${pageContext.request.contextPath}/login" style="color: #1e3a8a; font-weight: 600; text-decoration: none;">Sign in here</a>
        </div>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
