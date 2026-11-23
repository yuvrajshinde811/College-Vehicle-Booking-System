package com.yuvraj.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Booking {
    private int requestId; // also known as bookingId
    private int userId;
    private String purpose;
    private String source; // pickupLocation
    private String destination;
    private Date journeyDate;
    private Time requestedStartTime;
    private Time requestedEndTime;
    private int passengerCount;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED
    private Integer reviewedBy;
    private Timestamp reviewedAt;
    private Timestamp createdAt;

    // Joined fields for display
    private String requesterName;
    private String requesterRole;
    private String department;
    private String requesterPhone;

    // Trip details if allocated
    private Integer tripId;
    private String vehicleNumber;
    private String vehicleType;
    private String driverName;
    private String driverPhone;

    public Booking() {}

    public Booking(int userId, String purpose, String source, String destination, Date journeyDate, Time requestedStartTime, Time requestedEndTime, int passengerCount) {
        this.userId = userId;
        this.purpose = purpose;
        this.source = source;
        this.destination = destination;
        this.journeyDate = journeyDate;
        this.requestedStartTime = requestedStartTime;
        this.requestedEndTime = requestedEndTime;
        this.passengerCount = passengerCount;
        this.status = "PENDING";
    }

    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getBookingId() { return requestId; }
    public void setBookingId(int bookingId) { this.requestId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getPickupLocation() { return source; }
    public void setPickupLocation(String pickupLocation) { this.source = pickupLocation; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Date getJourneyDate() { return journeyDate; }
    public void setJourneyDate(Date journeyDate) { this.journeyDate = journeyDate; }

    public Time getRequestedStartTime() { return requestedStartTime; }
    public void setRequestedStartTime(Time requestedStartTime) { this.requestedStartTime = requestedStartTime; }

    public Time getRequestedEndTime() { return requestedEndTime; }
    public void setRequestedEndTime(Time requestedEndTime) { this.requestedEndTime = requestedEndTime; }

    public int getPassengerCount() { return passengerCount; }
    public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Integer reviewedBy) { this.reviewedBy = reviewedBy; }

    public Timestamp getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Timestamp reviewedAt) { this.reviewedAt = reviewedAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getRequesterRole() { return requesterRole; }
    public void setRequesterRole(String requesterRole) { this.requesterRole = requesterRole; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRequesterPhone() { return requesterPhone; }
    public void setRequesterPhone(String requesterPhone) { this.requesterPhone = requesterPhone; }

    public Integer getTripId() { return tripId; }
    public void setTripId(Integer tripId) { this.tripId = tripId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public boolean isPending() { return "PENDING".equalsIgnoreCase(status); }
    public boolean isApproved() { return "APPROVED".equalsIgnoreCase(status); }
    public boolean isRejected() { return "REJECTED".equalsIgnoreCase(status); }
    public boolean isCompleted() { return "COMPLETED".equalsIgnoreCase(status); }
    public boolean isCancelled() { return "CANCELLED".equalsIgnoreCase(status); }
}
