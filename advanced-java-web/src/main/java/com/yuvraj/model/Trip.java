package com.yuvraj.model;

import java.sql.Timestamp;

public class Trip {
    private int tripId;
    private int requestId;
    private int vehicleId;
    private int driverId;
    private Timestamp scheduledStart;
    private Timestamp scheduledEnd;
    private Timestamp actualStart;
    private Timestamp actualEnd;
    private String tripStatus; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    private String remarks;
    private Timestamp createdAt;

    // Joined display fields
    private String requesterName;
    private String department;
    private String purpose;
    private String source;
    private String destination;
    private String vehicleNumber;
    private String vehicleType;
    private int capacity;
    private String driverName;
    private String driverPhone;

    public Trip() {}

    public Trip(int requestId, int vehicleId, int driverId, Timestamp scheduledStart, Timestamp scheduledEnd, String remarks) {
        this.requestId = requestId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.tripStatus = "SCHEDULED";
        this.remarks = remarks;
    }

    // Getters and Setters
    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getBookingId() { return requestId; }
    public void setBookingId(int bookingId) { this.requestId = bookingId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public Timestamp getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(Timestamp scheduledStart) { this.scheduledStart = scheduledStart; }

    public Timestamp getScheduledEnd() { return scheduledEnd; }
    public void setScheduledEnd(Timestamp scheduledEnd) { this.scheduledEnd = scheduledEnd; }

    public Timestamp getActualStart() { return actualStart; }
    public void setActualStart(Timestamp actualStart) { this.actualStart = actualStart; }

    public Timestamp getActualEnd() { return actualEnd; }
    public void setActualEnd(Timestamp actualEnd) { this.actualEnd = actualEnd; }

    public String getTripStatus() { return tripStatus; }
    public void setTripStatus(String tripStatus) { this.tripStatus = tripStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public boolean isScheduled() { return "SCHEDULED".equalsIgnoreCase(tripStatus); }
    public boolean isInProgress() { return "IN_PROGRESS".equalsIgnoreCase(tripStatus); }
    public boolean isCompleted() { return "COMPLETED".equalsIgnoreCase(tripStatus); }
    public boolean isCancelled() { return "CANCELLED".equalsIgnoreCase(tripStatus); }
}
