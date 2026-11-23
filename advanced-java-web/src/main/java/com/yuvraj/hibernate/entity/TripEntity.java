package com.yuvraj.hibernate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "trips")
public class TripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private int tripId;

    @OneToOne
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private BookingEntity booking;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverEntity driver;

    @Column(name = "scheduled_start", nullable = false)
    private Timestamp scheduledStart;

    @Column(name = "scheduled_end", nullable = false)
    private Timestamp scheduledEnd;

    @Column(name = "actual_start")
    private Timestamp actualStart;

    @Column(name = "actual_end")
    private Timestamp actualEnd;

    @Column(name = "trip_status", length = 20)
    private String tripStatus = "SCHEDULED";

    @Column(name = "remarks", length = 255)
    private String remarks;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    public TripEntity() {}

    public TripEntity(BookingEntity booking, VehicleEntity vehicle, DriverEntity driver, Timestamp scheduledStart, Timestamp scheduledEnd, String remarks) {
        this.booking = booking;
        this.vehicle = vehicle;
        this.driver = driver;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.tripStatus = "SCHEDULED";
        this.remarks = remarks;
    }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public BookingEntity getBooking() { return booking; }
    public void setBooking(BookingEntity booking) { this.booking = booking; }

    public VehicleEntity getVehicle() { return vehicle; }
    public void setVehicle(VehicleEntity vehicle) { this.vehicle = vehicle; }

    public DriverEntity getDriver() { return driver; }
    public void setDriver(DriverEntity driver) { this.driver = driver; }

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
}
