-- ========================================================
-- COLLEGE VEHICLE BOOKING SYSTEM
-- Script: 03_insert_sample_data.sql
-- Description: Inserts realistic seed data for users, vehicles,
--              drivers, booking requests, and trips.
--
-- Test Account Credentials:
-- Admin:   admin@college.edu           Password: Admin@123
-- Staff:   prof.sharma@college.edu     Password: Staff@123
-- Staff:   dr.patil@college.edu        Password: Staff@123
-- Student: yuvraj.student@college.edu  Password: Student@123
-- Student: amit.deshmukh@college.edu   Password: Student@123
-- Student: sneha.kulkarni@college.edu  Password: Student@123
-- ========================================================

USE college_vehicle_booking;

-- Disable foreign key checks for clean truncation and re-seeding
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE trips;
TRUNCATE TABLE booking_requests;
TRUNCATE TABLE drivers;
TRUNCATE TABLE vehicles;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. INSERT USERS
-- All password hashes generated via com.yuvraj.database.PasswordUtil (PBKDF2WithHmacSHA256)
INSERT INTO users (user_id, full_name, email, password_hash, role, department, phone, account_status) VALUES
(1, 'System Administrator', 'admin@college.edu', '210000:CUwJO+7a6IiMh9hxHc1PtA==:QJhqtchdSZzqPBxXk1fFrQ2pmqP3ilR+4s/ker4JCzE=', 'ADMIN', 'Administration', '9876543210', 'ACTIVE'),
(2, 'Prof. Rajesh Sharma', 'prof.sharma@college.edu', '210000:xGjEQf/tR/EpC4yzWNnHyg==:GATbz978C6ojmDzn9MmYlHM2ZVu+Q21H5TDz0b7jjJQ=', 'STAFF', 'Computer Applications', '9876543211', 'ACTIVE'),
(3, 'Dr. Sunita Patil', 'dr.patil@college.edu', '210000:xGjEQf/tR/EpC4yzWNnHyg==:GATbz978C6ojmDzn9MmYlHM2ZVu+Q21H5TDz0b7jjJQ=', 'STAFF', 'Management Studies', '9876543212', 'ACTIVE'),
(4, 'Yuvraj Shinde', 'yuvraj.student@college.edu', '210000:Jp4aHD1a/h+UAnmcLh+Okg==:KDS/NGi5tOt/9PaNHfnoMIqFomL4uWfxHRokFVzlaKE=', 'STUDENT', 'MCA', '9876543213', 'ACTIVE'),
(5, 'Amit Deshmukh', 'amit.deshmukh@college.edu', '210000:Jp4aHD1a/h+UAnmcLh+Okg==:KDS/NGi5tOt/9PaNHfnoMIqFomL4uWfxHRokFVzlaKE=', 'STUDENT', 'MCA', '9876543214', 'ACTIVE'),
(6, 'Sneha Kulkarni', 'sneha.kulkarni@college.edu', '210000:Jp4aHD1a/h+UAnmcLh+Okg==:KDS/NGi5tOt/9PaNHfnoMIqFomL4uWfxHRokFVzlaKE=', 'STUDENT', 'MBA', '9876543215', 'ACTIVE');

-- 2. INSERT VEHICLES
INSERT INTO vehicles (vehicle_id, vehicle_number, vehicle_type, capacity, status) VALUES
(1, 'MH-12-AB-1001', 'Bus (Large)', 45, 'ACTIVE'),
(2, 'MH-12-CD-2002', 'Mini Bus', 25, 'ACTIVE'),
(3, 'MH-12-EF-3003', 'Van (Tempo Traveller)', 14, 'ACTIVE'),
(4, 'MH-12-GH-4004', 'SUV (Innova)', 7, 'ACTIVE'),
(5, 'MH-12-IJ-5005', 'Sedan (Dzire)', 4, 'ACTIVE'),
(6, 'MH-12-KL-6006', 'Bus (Campus)', 40, 'INACTIVE');

-- 3. INSERT DRIVERS
INSERT INTO drivers (driver_id, driver_name, phone, license_number, status) VALUES
(1, 'Ramesh Pawar', '9822011223', 'MH12-2015-001234', 'ACTIVE'),
(2, 'Suresh Gaikwad', '9822022334', 'MH12-2017-005678', 'ACTIVE'),
(3, 'Mahesh Shinde', '9822033445', 'MH12-2019-009012', 'ACTIVE'),
(4, 'Ganesh Jadhav', '9822044556', 'MH12-2021-003456', 'ACTIVE'),
(5, 'Dinesh Kadam', '9822055667', 'MH12-2014-007890', 'INACTIVE');

-- 4. INSERT BOOKING REQUESTS
INSERT INTO booking_requests (
    request_id, user_id, purpose, source, destination, 
    journey_date, requested_start_time, requested_end_time, passenger_count, 
    status, reviewed_by, reviewed_at
) VALUES
(1, 4, 'Industrial Visit to IT Park Pune', 'College Main Campus', 'Hinjawadi Phase 1, Pune', CURDATE() + INTERVAL 1 DAY, '09:00:00', '17:00:00', 22, 'APPROVED', 1, NOW()),
(2, 2, 'Guest Lecture Pickup for University Seminar', 'College Campus', 'Pune Railway Station', CURDATE() + INTERVAL 2 DAY, '10:00:00', '13:00:00', 4, 'APPROVED', 1, NOW()),
(3, 5, 'Inter-College Basketball Championship', 'College Gymkhana', 'Balewadi Sports Complex', CURDATE() + INTERVAL 3 DAY, '08:00:00', '16:00:00', 14, 'PENDING', NULL, NULL),
(4, 3, 'Academic Conference at University of Pune', 'Management Dept', 'Savitribai Phule Pune University', CURDATE() + INTERVAL 4 DAY, '09:30:00', '14:30:00', 6, 'PENDING', NULL, NULL),
(5, 6, 'MBA Field Survey & Market Research', 'Campus Gate 2', 'MIDC Bhosari', CURDATE() - INTERVAL 1 DAY, '10:00:00', '15:00:00', 5, 'COMPLETED', 1, NOW() - INTERVAL 2 DAY),
(6, 4, 'Personal Weekend Trip', 'Hostel', 'Lonavala', CURDATE() - INTERVAL 2 DAY, '07:00:00', '20:00:00', 10, 'REJECTED', 1, NOW() - INTERVAL 2 DAY);

-- 5. INSERT TRIPS (Allocated to approved bookings)
INSERT INTO trips (
    trip_id, request_id, vehicle_id, driver_id, 
    scheduled_start, scheduled_end, actual_start, actual_end, 
    trip_status, remarks
) VALUES
(1, 1, 2, 1, 
 CONCAT(CURDATE() + INTERVAL 1 DAY, ' 09:00:00'), CONCAT(CURDATE() + INTERVAL 1 DAY, ' 17:00:00'), 
 NULL, NULL, 'SCHEDULED', 'Approved for MCA Dept Industrial Visit. Vehicle: Mini Bus (25 seats)'),

(2, 2, 5, 2, 
 CONCAT(CURDATE() + INTERVAL 2 DAY, ' 10:00:00'), CONCAT(CURDATE() + INTERVAL 2 DAY, ' 13:00:00'), 
 NULL, NULL, 'SCHEDULED', 'Guest pickup from Pune Railway Station. Vehicle: Sedan (4 seats)'),

(3, 5, 4, 3, 
 CONCAT(CURDATE() - INTERVAL 1 DAY, ' 10:00:00'), CONCAT(CURDATE() - INTERVAL 1 DAY, ' 15:00:00'), 
 CONCAT(CURDATE() - INTERVAL 1 DAY, ' 09:55:00'), CONCAT(CURDATE() - INTERVAL 1 DAY, ' 15:10:00'), 
 'COMPLETED', 'Trip completed successfully without delays.');
