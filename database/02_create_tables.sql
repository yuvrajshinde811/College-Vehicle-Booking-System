-- ========================================================
-- COLLEGE VEHICLE BOOKING SYSTEM
-- Script: 02_create_tables.sql
-- Description: Defines normalized relational schema with constraints,
--              enhances booking_requests & trips, and creates views.
-- ========================================================

USE college_vehicle_booking;

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'STAFF', 'ADMIN') NOT NULL,
    department VARCHAR(100),
    phone VARCHAR(15),
    account_status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
);

-- 2. VEHICLES TABLE
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_vehicle_capacity CHECK (capacity > 0),
    INDEX idx_vehicle_status (status)
);

-- 3. DRIVERS TABLE
CREATE TABLE IF NOT EXISTS drivers (
    driver_id INT PRIMARY KEY AUTO_INCREMENT,
    driver_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_driver_status (status)
);

-- 4. BOOKING REQUESTS TABLE
CREATE TABLE IF NOT EXISTS booking_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    source VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    journey_date DATE NOT NULL,
    requested_start_time TIME NOT NULL,
    requested_end_time TIME NOT NULL,
    passenger_count INT NOT NULL DEFAULT 1,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    reviewed_by INT,
    reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_request_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,

    CONSTRAINT fk_request_admin
        FOREIGN KEY (reviewed_by) REFERENCES users(user_id) ON DELETE SET NULL,

    CONSTRAINT chk_request_time
        CHECK (requested_end_time > requested_start_time),

    CONSTRAINT chk_passenger_count
        CHECK (passenger_count > 0),

    INDEX idx_booking_user (user_id),
    INDEX idx_booking_status (status),
    INDEX idx_journey_date (journey_date)
);

-- 5. TRIPS TABLE
CREATE TABLE IF NOT EXISTS trips (
    trip_id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL UNIQUE,
    vehicle_id INT NOT NULL,
    driver_id INT NOT NULL,

    scheduled_start DATETIME NOT NULL,
    scheduled_end DATETIME NOT NULL,

    actual_start DATETIME,
    actual_end DATETIME,

    trip_status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trip_request
        FOREIGN KEY (request_id) REFERENCES booking_requests(request_id) ON DELETE CASCADE,

    CONSTRAINT fk_trip_vehicle
        FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE RESTRICT,

    CONSTRAINT fk_trip_driver
        FOREIGN KEY (driver_id) REFERENCES drivers(driver_id) ON DELETE RESTRICT,

    CONSTRAINT chk_trip_schedule
        CHECK (scheduled_end > scheduled_start),

    INDEX idx_trip_vehicle (vehicle_id),
    INDEX idx_trip_driver (driver_id),
    INDEX idx_trip_status (trip_status),
    INDEX idx_trip_schedule (scheduled_start, scheduled_end)
);

-- Safe Alterations for Pre-existing Tables
-- Ensure passenger_count exists on booking_requests
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'college_vehicle_booking' AND TABLE_NAME = 'booking_requests' AND COLUMN_NAME = 'passenger_count');
SET @sql_stmt = IF(@col_exists = 0, 'ALTER TABLE booking_requests ADD COLUMN passenger_count INT NOT NULL DEFAULT 1 AFTER requested_end_time', 'SELECT 1');
PREPARE stmt FROM @sql_stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update status ENUM to include COMPLETED on booking_requests
ALTER TABLE booking_requests MODIFY COLUMN status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING';

-- Ensure remarks exists on trips
SET @col_remarks = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'college_vehicle_booking' AND TABLE_NAME = 'trips' AND COLUMN_NAME = 'remarks');
SET @sql_stmt2 = IF(@col_remarks = 0, 'ALTER TABLE trips ADD COLUMN remarks VARCHAR(255) AFTER trip_status', 'SELECT 1');
PREPARE stmt2 FROM @sql_stmt2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- 6. BOOKINGS VIEW (Aliases booking_requests cleanly for bookings terminology)
CREATE OR REPLACE VIEW bookings AS
SELECT 
    request_id AS booking_id,
    user_id,
    journey_date,
    source AS pickup_location,
    destination,
    purpose,
    passenger_count,
    status,
    created_at AS requested_at
FROM booking_requests;
