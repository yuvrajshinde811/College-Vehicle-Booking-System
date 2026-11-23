package com.yuvraj.boot.controller;

import com.yuvraj.boot.entity.Trip;
import com.yuvraj.boot.repository.TripRepository;
import com.yuvraj.boot.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripRestController {

    private final TripRepository tripRepository;
    private final BookingService bookingService;

    public TripRestController(TripRepository tripRepository, BookingService bookingService) {
        this.tripRepository = tripRepository;
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getAllTrips() {
        return ResponseEntity.ok(tripRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable int id) {
        return tripRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/allocate")
    public ResponseEntity<?> allocateTrip(
            @RequestParam int requestId,
            @RequestParam int vehicleId,
            @RequestParam int driverId,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(defaultValue = "1") int adminId,
            @RequestParam(required = false) String remarks) {

        try {
            Timestamp startTs = Timestamp.valueOf(start);
            Timestamp endTs = Timestamp.valueOf(end);
            Trip allocated = bookingService.allocateTrip(requestId, vehicleId, driverId, startTs, endTs, adminId, remarks);
            return ResponseEntity.status(HttpStatus.CREATED).body(allocated);
        } catch (IllegalStateException e) {
            // Overlapping vehicle/driver conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"conflict\": \"" + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeTrip(@PathVariable int id,
                                         @RequestParam(required = false) String remarks) {
        try {
            Trip completed = bookingService.completeTrip(id, remarks);
            return ResponseEntity.ok(completed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
