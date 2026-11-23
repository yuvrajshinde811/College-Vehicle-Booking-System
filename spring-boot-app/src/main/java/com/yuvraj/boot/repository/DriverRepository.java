package com.yuvraj.boot.repository;

import com.yuvraj.boot.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

    List<Driver> findByStatus(String status);

    /**
     * Demonstrates JPQL:
     * Finds active drivers with no overlapping trips.
     */
    @Query("SELECT d FROM Driver d WHERE d.status = 'ACTIVE' " +
           "AND d.driverId NOT IN (" +
           "  SELECT t.driver.driverId FROM Trip t " +
           "  WHERE t.tripStatus IN ('SCHEDULED', 'IN_PROGRESS') " +
           "  AND (t.scheduledStart < :endT AND t.scheduledEnd > :startT)" +
           ") ORDER BY d.driverName ASC")
    List<Driver> findAvailableDrivers(@Param("startT") Timestamp startT,
                                      @Param("endT") Timestamp endT);
}
