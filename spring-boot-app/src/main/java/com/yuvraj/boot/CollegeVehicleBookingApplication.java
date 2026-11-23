package com.yuvraj.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application entrypoint demonstrating:
 * - Spring Boot Auto-Configuration
 * - Spring Boot Starter Dependencies
 * - Task 3: JPA & Spring MVC
 * - Task 4: Spring Boot REST API & Microservices Concepts
 */
@SpringBootApplication
public class CollegeVehicleBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollegeVehicleBookingApplication.class, args);
        System.out.println("==================================================================");
        System.out.println(" College Vehicle Booking System (Spring Boot) started on port 8082 ");
        System.out.println(" REST API Documentation / Endpoints available at:                  ");
        System.out.println("  - Users:    http://localhost:8082/api/users                      ");
        System.out.println("  - Vehicles: http://localhost:8082/api/vehicles                   ");
        System.out.println("  - Drivers:  http://localhost:8082/api/drivers                    ");
        System.out.println("  - Bookings: http://localhost:8082/api/bookings                   ");
        System.out.println("  - Trips:    http://localhost:8082/api/trips                      ");
        System.out.println("  - MVC Form: http://localhost:8082/mvc/bookings                   ");
        System.out.println("==================================================================");
    }
}
