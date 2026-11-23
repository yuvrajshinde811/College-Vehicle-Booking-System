<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- 
  Demonstrating JSP Action Tag (<jsp:include>) for dynamic request-time inclusion 
--%>
<jsp:include page="/common/header.jsp">
    <jsp:param name="title" value="Home — College Vehicle Booking System" />
</jsp:include>

<jsp:include page="/common/navbar.jsp" />

<div class="container">
    <div class="card" style="text-align: center; padding: 3rem 1.5rem; background: linear-gradient(180deg, #ffffff 0%, #f0fdfa 100%);">
        <h1 style="font-size: 2.25rem; color: #1e3a8a; margin-bottom: 0.75rem;">
            College Vehicle Booking System
        </h1>
        <p style="font-size: 1.15rem; color: #475569; max-width: 700px; margin: 0 auto 1.75rem;">
            An integrated, automated platform for Students, Staff, and Administrators to request, schedule, approve, and manage college fleet transportation.
        </p>

        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary" style="font-size: 1.1rem; padding: 0.75rem 2rem;">
                    Go to Your Dashboard &rarr;
                </a>
            </c:when>
            <c:otherwise>
                <div style="display: flex; justify-content: center; gap: 1rem;">
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary" style="font-size: 1.05rem; padding: 0.75rem 1.75rem;">
                        Login to Portal
                    </a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-secondary" style="font-size: 1.05rem; padding: 0.75rem 1.75rem;">
                        New Registration
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <span class="stat-label">&#128101; For Students</span>
            <p style="margin-top: 0.5rem; font-size: 0.95rem; color: #475569;">
                Book college buses and vans for authorized industrial visits, sports tournaments, and technical conferences.
            </p>
        </div>

        <div class="stat-card teal">
            <span class="stat-label">&#127891; For Staff</span>
            <p style="margin-top: 0.5rem; font-size: 0.95rem; color: #475569;">
                Request official transport for university seminars, field surveys, guest lecture pickups, and department duties.
            </p>
        </div>

        <div class="stat-card amber">
            <span class="stat-label">&#128737;&#65039; For Administrators</span>
            <p style="margin-top: 0.5rem; font-size: 0.95rem; color: #475569;">
                Review pending requests, verify capacity, check schedule overlaps, and allocate vehicles and drivers atomically.
            </p>
        </div>

        <div class="stat-card green">
            <span class="stat-label">&#128663; Fleet Management</span>
            <p style="margin-top: 0.5rem; font-size: 0.95rem; color: #475569;">
                Maintain active college buses, vans, cars, and licensed drivers with real-time status and trip history.
            </p>
        </div>
    </div>

    <!-- Quick Demo Credentials Box -->
    <div class="card" style="background-color: #f8fafc; border: 1px dashed #cbd5e1;">
        <h3 style="font-size: 1.1rem; color: #0f172a; margin-bottom: 0.75rem;">
            &#128273; Pre-configured Demonstration Accounts
        </h3>
        <p style="font-size: 0.9rem; color: #64748b; margin-bottom: 1rem;">
            You can immediately log in with any of the seeded demonstration accounts below:
        </p>
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1rem; font-size: 0.9rem;">
            <div style="background: #ffffff; padding: 0.75rem 1rem; border-radius: 6px; border: 1px solid #e2e8f0;">
                <span class="badge badge-approved">Admin</span>
                <div style="margin-top: 0.35rem;"><strong>Email:</strong> admin@college.edu</div>
                <div><strong>Password:</strong> Admin@123</div>
            </div>
            <div style="background: #ffffff; padding: 0.75rem 1rem; border-radius: 6px; border: 1px solid #e2e8f0;">
                <span class="badge badge-pending">Staff</span>
                <div style="margin-top: 0.35rem;"><strong>Email:</strong> prof.sharma@college.edu</div>
                <div><strong>Password:</strong> Staff@123</div>
            </div>
            <div style="background: #ffffff; padding: 0.75rem 1rem; border-radius: 6px; border: 1px solid #e2e8f0;">
                <span class="badge badge-completed">Student</span>
                <div style="margin-top: 0.35rem;"><strong>Email:</strong> yuvraj.student@college.edu</div>
                <div><strong>Password:</strong> Student@123</div>
            </div>
        </div>
    </div>
</div>

<%-- 
  Demonstrating JSP Directive (<%@ include file="..." %>) for translation-time static inclusion
--%>
<%@ include file="/common/footer.jsp" %>
