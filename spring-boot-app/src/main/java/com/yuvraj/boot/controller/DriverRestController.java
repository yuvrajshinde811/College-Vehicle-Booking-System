package com.yuvraj.boot.controller;

import com.yuvraj.boot.entity.Driver;
import com.yuvraj.boot.service.FleetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverRestController {

    private final FleetService fleetService;

    public DriverRestController(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(fleetService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable int id) {
        return fleetService.getDriverById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Driver>> getAvailableDrivers(
            @RequestParam String start,
            @RequestParam String end) {
        try {
            Timestamp startTs = Timestamp.valueOf(start);
            Timestamp endTs = Timestamp.valueOf(end);
            return ResponseEntity.ok(fleetService.getAvailableDrivers(startTs, endTs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody Driver driver) {
        Driver saved = fleetService.saveDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable int id, @Valid @RequestBody Driver updated) {
        return fleetService.getDriverById(id)
                .map(d -> {
                    d.setDriverName(updated.getDriverName());
                    d.setPhone(updated.getPhone());
                    d.setLicenseNumber(updated.getLicenseNumber());
                    d.setStatus(updated.getStatus());
                    return ResponseEntity.ok(fleetService.saveDriver(d));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable int id) {
        return fleetService.getDriverById(id)
                .map(d -> {
                    d.setStatus("INACTIVE");
                    fleetService.saveDriver(d);
                    return ResponseEntity.ok().body("{\"message\": \"Driver deactivated successfully\"}");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
