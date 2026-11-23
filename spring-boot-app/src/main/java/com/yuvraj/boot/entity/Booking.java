package com.yuvraj.boot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "booking_requests")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private int requestId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Purpose cannot be blank")
    @Column(name = "purpose", nullable = false, length = 255)
    private String purpose;

    @NotBlank(message = "Pickup location cannot be blank")
    @Column(name = "source", nullable = false, length = 150)
    private String source;

    @NotBlank(message = "Destination cannot be blank")
    @Column(name = "destination", nullable = false, length = 150)
    private String destination;

    @NotNull(message = "Journey date cannot be null")
    @Column(name = "journey_date", nullable = false)
    private Date journeyDate;

    @NotNull(message = "Start time cannot be null")
    @Column(name = "requested_start_time", nullable = false)
    private Time requestedStartTime;

    @NotNull(message = "End time cannot be null")
    @Column(name = "requested_end_time", nullable = false)
    private Time requestedEndTime;

    @Min(value = 1, message = "Passenger count must be at least 1")
    @Column(name = "passenger_count", nullable = false)
    private int passengerCount = 1;

    @Column(name = "status", length = 20)
    private String status = "PENDING";

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private Timestamp reviewedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    public Booking() {}

    public Booking(User user, String purpose, String source, String destination, Date journeyDate, Time requestedStartTime, Time requestedEndTime, int passengerCount) {
        this.user = user;
        this.purpose = purpose;
        this.source = source;
        this.destination = destination;
        this.journeyDate = journeyDate;
        this.requestedStartTime = requestedStartTime;
        this.requestedEndTime = requestedEndTime;
        this.passengerCount = passengerCount;
        this.status = "PENDING";
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

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

    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }

    public Timestamp getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Timestamp reviewedAt) { this.reviewedAt = reviewedAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
