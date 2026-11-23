package com.yuvraj.boot.repository;

import com.yuvraj.boot.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    List<Vehicle> findByStatus(String status);

    /**
     * Demonstrates JPQL (Java Persistence Query Language):
     * Finds active vehicles with capacity >= minCapacity that are NOT booked for overlapping trips.
     */
    @Query("SELECT v FROM Vehicle v WHERE v.status = 'ACTIVE' AND v.capacity >= :minCapacity " +
           "AND v.vehicleId NOT IN (" +
           "  SELECT t.vehicle.vehicleId FROM Trip t " +
           "  WHERE t.tripStatus IN ('SCHEDULED', 'IN_PROGRESS') " +
           "  AND (t.scheduledStart < :endT AND t.scheduledEnd > :startT)" +
           ") ORDER BY v.capacity ASC")
    List<Vehicle> findAvailableVehicles(@Param("minCapacity") int minCapacity,
                                       @Param("startT") Timestamp startT,
                                       @Param("endT") Timestamp endT);
}
