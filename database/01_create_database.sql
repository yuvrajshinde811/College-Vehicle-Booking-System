
-- ==========================================
-- COLLEGE VEHICLE BOOKING SYSTEM
-- Database: college_vehicle_booking
-- MySQL 8
-- ==========================================

CREATE DATABASE IF NOT EXISTS college_vehicle_booking;

USE college_vehicle_booking;


-- 1. USERS TABLE
-- Stores Student, Staff and Admin accounts

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'STAFF', 'ADMIN') NOT NULL,
    department VARCHAR(100),
    phone VARCHAR(15),
    account_status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- 2. VEHICLES TABLE
-- Stores college vehicle information

CREATE TABLE vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',

    CONSTRAINT chk_vehicle_capacity CHECK (capacity > 0)
);


-- 3. DRIVERS TABLE
-- Stores driver information

CREATE TABLE drivers (
    driver_id INT PRIMARY KEY AUTO_INCREMENT,
    driver_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE'
);


-- 4. BOOKING REQUESTS TABLE
-- Stores requests submitted by students and staff

CREATE TABLE booking_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    source VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    journey_date DATE NOT NULL,
    requested_start_time TIME NOT NULL,
    requested_end_time TIME NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')
        DEFAULT 'PENDING',
    reviewed_by INT,
    reviewed_at TIMESTAMP NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_request_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),

    CONSTRAINT fk_request_admin
        FOREIGN KEY (reviewed_by) REFERENCES users(user_id),

    CONSTRAINT chk_request_time
        CHECK (requested_end_time > requested_start_time)
);


-- 5. TRIPS TABLE
-- Stores approved booking allocation and trip details

CREATE TABLE trips (
    trip_id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL UNIQUE,
    vehicle_id INT NOT NULL,
    driver_id INT NOT NULL,

    scheduled_start DATETIME NOT NULL,
    scheduled_end DATETIME NOT NULL,

    actual_start DATETIME,
    actual_end DATETIME,

    trip_status ENUM(
        'SCHEDULED',
        'IN_PROGRESS',
        'COMPLETED',
        'CANCELLED'
    ) DEFAULT 'SCHEDULED',

    CONSTRAINT fk_trip_request
        FOREIGN KEY (request_id)
        REFERENCES booking_requests(request_id),

    CONSTRAINT fk_trip_vehicle
        FOREIGN KEY (vehicle_id)
        REFERENCES vehicles(vehicle_id),

    CONSTRAINT fk_trip_driver
        FOREIGN KEY (driver_id)
        REFERENCES drivers(driver_id),

    CONSTRAINT chk_trip_schedule
        CHECK (scheduled_end > scheduled_start)
);


-- ==========================================
-- DATABASE CREATED SUCCESSFULLY
-- ==========================================

SHOW TABLES;
