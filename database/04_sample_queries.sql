-- ========================================================
-- COLLEGE VEHICLE BOOKING SYSTEM
-- Script: 04_sample_queries.sql
-- Description: Essential SQL queries for reporting, authentication,
--              booking management, and conflict checking.
-- ========================================================

USE college_vehicle_booking;

-- 1. USER AUTHENTICATION QUERY
-- Retrieve user credentials by email for login verification
SELECT user_id, full_name, email, password_hash, role, department, phone, account_status
FROM users
WHERE email = 'admin@college.edu' AND account_status = 'ACTIVE';

-- 2. ADMIN: VIEW ALL PENDING BOOKING REQUESTS
-- Displays student/staff requests waiting for administrative review
SELECT 
    b.request_id,
    u.full_name AS requester_name,
    u.role AS requester_role,
    u.department,
    b.purpose,
    b.source AS pickup_location,
    b.destination,
    b.journey_date,
    b.requested_start_time,
    b.requested_end_time,
    b.passenger_count,
    b.status,
    b.created_at
FROM booking_requests b
JOIN users u ON b.user_id = u.user_id
WHERE b.status = 'PENDING'
ORDER BY b.journey_date ASC, b.requested_start_time ASC;

-- 3. USER: VIEW PERSONAL BOOKING HISTORY
-- Filter bookings for a specific user (e.g., student user_id = 4)
SELECT 
    b.request_id,
    b.purpose,
    b.source,
    b.destination,
    b.journey_date,
    b.requested_start_time,
    b.requested_end_time,
    b.passenger_count,
    b.status,
    t.trip_id,
    v.vehicle_number,
    v.vehicle_type,
    d.driver_name,
    d.phone AS driver_phone
FROM booking_requests b
LEFT JOIN trips t ON b.request_id = t.request_id
LEFT JOIN vehicles v ON t.vehicle_id = v.vehicle_id
LEFT JOIN drivers d ON t.driver_id = d.driver_id
WHERE b.user_id = 4
ORDER BY b.created_at DESC;

-- 4. VIEW ALL ALLOCATED TRIPS WITH COMPLETE DETAILS
-- Multi-table JOIN combining Trips, Requests, Vehicles, Drivers, and Requesters
SELECT 
    t.trip_id,
    b.request_id,
    u.full_name AS requester_name,
    u.department,
    b.purpose,
    b.source,
    b.destination,
    v.vehicle_number,
    v.vehicle_type,
    v.capacity,
    d.driver_name,
    d.phone AS driver_phone,
    t.scheduled_start,
    t.scheduled_end,
    t.trip_status,
    t.remarks
FROM trips t
JOIN booking_requests b ON t.request_id = b.request_id
JOIN users u ON b.user_id = u.user_id
JOIN vehicles v ON t.vehicle_id = v.vehicle_id
JOIN drivers d ON t.driver_id = d.driver_id
ORDER BY t.scheduled_start ASC;

-- 5. CHECK VEHICLE AVAILABILITY FOR A TIME WINDOW
-- Find active vehicles with sufficient capacity that DO NOT have overlapping scheduled trips
-- Example parameters: Journey Start: 2026-11-25 09:00:00, End: 2026-11-25 17:00:00, Passengers: 15
SELECT v.vehicle_id, v.vehicle_number, v.vehicle_type, v.capacity
FROM vehicles v
WHERE v.status = 'ACTIVE'
  AND v.capacity >= 15
  AND v.vehicle_id NOT IN (
      SELECT t.vehicle_id
      FROM trips t
      WHERE t.trip_status IN ('SCHEDULED', 'IN_PROGRESS')
        AND (t.scheduled_start < '2026-11-25 17:00:00' AND t.scheduled_end > '2026-11-25 09:00:00')
  );

-- 6. CHECK DRIVER AVAILABILITY FOR A TIME WINDOW
-- Find active drivers who DO NOT have overlapping trips
SELECT d.driver_id, d.driver_name, d.phone, d.license_number
FROM drivers d
WHERE d.status = 'ACTIVE'
  AND d.driver_id NOT IN (
      SELECT t.driver_id
      FROM trips t
      WHERE t.trip_status IN ('SCHEDULED', 'IN_PROGRESS')
        AND (t.scheduled_start < '2026-11-25 17:00:00' AND t.scheduled_end > '2026-11-25 09:00:00')
  );

-- 7. DASHBOARD STATISTICS: BOOKING SUMMARY BY STATUS
SELECT 
    status,
    COUNT(*) AS total_count
FROM booking_requests
GROUP BY status;

-- 8. DASHBOARD STATISTICS: FLEET SUMMARY
SELECT 
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM vehicles WHERE status = 'ACTIVE') AS active_vehicles,
    (SELECT COUNT(*) FROM drivers WHERE status = 'ACTIVE') AS active_drivers,
    (SELECT COUNT(*) FROM booking_requests WHERE status = 'PENDING') AS pending_requests,
    (SELECT COUNT(*) FROM trips WHERE trip_status = 'SCHEDULED') AS scheduled_trips,
    (SELECT COUNT(*) FROM trips WHERE trip_status = 'COMPLETED') AS completed_trips;
