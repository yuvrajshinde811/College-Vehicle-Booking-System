package com.yuvraj.boot.repository;

import com.yuvraj.boot.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {
    Optional<Trip> findByBookingRequestId(int requestId);
    List<Trip> findByTripStatus(String status);
}
