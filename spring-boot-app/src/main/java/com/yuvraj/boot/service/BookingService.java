package com.yuvraj.boot.service;

import com.yuvraj.boot.entity.Booking;
import com.yuvraj.boot.entity.Driver;
import com.yuvraj.boot.entity.Trip;
import com.yuvraj.boot.entity.User;
import com.yuvraj.boot.entity.Vehicle;
import com.yuvraj.boot.repository.BookingRepository;
import com.yuvraj.boot.repository.DriverRepository;
import com.yuvraj.boot.repository.TripRepository;
import com.yuvraj.boot.repository.UserRepository;
import com.yuvraj.boot.repository.VehicleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * BookingService demonstrates:
 * 1. Spring IoC (Inversion of Control) & Dependency Injection
 * 2. EntityManager injection with @PersistenceContext
 * 3. Declarative Transaction Management with @Transactional
 * 4. Business rule enforcement
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Constructor Dependency Injection (Spring IoC best practice)
    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          DriverRepository driverRepository,
                          TripRepository tripRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(int requestId) {
        return bookingRepository.findById(requestId);
    }

    public List<Booking> getBookingsByUserId(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getPendingBookings() {
        return bookingRepository.findByStatusOrderByJourneyDateAsc("PENDING");
    }

    @Transactional
    public Booking createBooking(Booking booking) {
        booking.setStatus("PENDING");
        return bookingRepository.save(booking);
    }

    @Transactional
    public boolean updateStatus(int requestId, String status, int adminUserId) {
        Optional<Booking> opt = bookingRepository.findById(requestId);
        if (opt.isPresent()) {
            Booking b = opt.get();
            b.setStatus(status.toUpperCase());
            userRepository.findById(adminUserId).ifPresent(b::setReviewedBy);
            b.setReviewedAt(new Timestamp(System.currentTimeMillis()));
            bookingRepository.save(b);
            return true;
        }
        return false;
    }

    /**
     * Demonstrates Spring @Transactional:
     * Allocates vehicle and driver, checks schedule conflicts,
     * updates booking to APPROVED and creates Trip atomically.
     */
    @Transactional
    public Trip allocateTrip(int requestId, int vehicleId, int driverId,
                             Timestamp start, Timestamp end, int adminUserId, String remarks) {

        Booking booking = bookingRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + requestId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + driverId));

        if (!"ACTIVE".equalsIgnoreCase(vehicle.getStatus())) {
            throw new IllegalStateException("Vehicle is inactive: " + vehicle.getVehicleNumber());
        }

        if (!"ACTIVE".equalsIgnoreCase(driver.getStatus())) {
            throw new IllegalStateException("Driver is inactive: " + driver.getDriverName());
        }

        if (vehicle.getCapacity() < booking.getPassengerCount()) {
            throw new IllegalArgumentException("Vehicle capacity (" + vehicle.getCapacity() +
                    ") is insufficient for " + booking.getPassengerCount() + " passengers.");
        }

        // Verify vehicle schedule conflict using EntityManager JPQL
        Long vehicleConflicts = entityManager.createQuery(
                "SELECT COUNT(t) FROM Trip t WHERE t.vehicle.vehicleId = :vId " +
                "AND t.tripStatus IN ('SCHEDULED', 'IN_PROGRESS') " +
                "AND (t.scheduledStart < :endT AND t.scheduledEnd > :startT)", Long.class)
                .setParameter("vId", vehicleId)
                .setParameter("endT", end)
                .setParameter("startT", start)
                .getSingleResult();

        if (vehicleConflicts > 0) {
            throw new IllegalStateException("Vehicle is already allocated to another trip during this time window.");
        }

        // Verify driver schedule conflict using EntityManager JPQL
        Long driverConflicts = entityManager.createQuery(
                "SELECT COUNT(t) FROM Trip t WHERE t.driver.driverId = :dId " +
                "AND t.tripStatus IN ('SCHEDULED', 'IN_PROGRESS') " +
                "AND (t.scheduledStart < :endT AND t.scheduledEnd > :startT)", Long.class)
                .setParameter("dId", driverId)
                .setParameter("endT", end)
                .setParameter("startT", start)
                .getSingleResult();

        if (driverConflicts > 0) {
            throw new IllegalStateException("Driver is already assigned to another trip during this time window.");
        }

        // Update booking status
        booking.setStatus("APPROVED");
        userRepository.findById(adminUserId).ifPresent(booking::setReviewedBy);
        booking.setReviewedAt(new Timestamp(System.currentTimeMillis()));
        bookingRepository.save(booking);

        // Create and persist Trip
        Trip trip = new Trip(booking, vehicle, driver, start, end, remarks);
        return tripRepository.save(trip);
    }

    @Transactional
    public Trip completeTrip(int tripId, String remarks) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        trip.setTripStatus("COMPLETED");
        trip.setActualEnd(new Timestamp(System.currentTimeMillis()));
        if (remarks != null && !remarks.isBlank()) {
            trip.setRemarks(remarks);
        }

        trip.getBooking().setStatus("COMPLETED");
        bookingRepository.save(trip.getBooking());

        return tripRepository.save(trip);
    }
}
