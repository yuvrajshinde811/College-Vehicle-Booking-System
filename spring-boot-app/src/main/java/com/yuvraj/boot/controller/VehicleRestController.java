package com.yuvraj.boot.controller;

import com.yuvraj.boot.entity.Vehicle;
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
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    private final FleetService fleetService;

    public VehicleRestController(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(fleetService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable int id) {
        return fleetService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles(
            @RequestParam(defaultValue = "1") int minCapacity,
            @RequestParam String start,
            @RequestParam String end) {
        try {
            Timestamp startTs = Timestamp.valueOf(start);
            Timestamp endTs = Timestamp.valueOf(end);
            return ResponseEntity.ok(fleetService.getAvailableVehicles(minCapacity, startTs, endTs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        Vehicle saved = fleetService.saveVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable int id, @Valid @RequestBody Vehicle updated) {
        return fleetService.getVehicleById(id)
                .map(v -> {
                    v.setVehicleNumber(updated.getVehicleNumber());
                    v.setVehicleType(updated.getVehicleType());
                    v.setCapacity(updated.getCapacity());
                    v.setStatus(updated.getStatus());
                    return ResponseEntity.ok(fleetService.saveVehicle(v));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable int id) {
        return fleetService.getVehicleById(id)
                .map(v -> {
                    v.setStatus("INACTIVE");
                    fleetService.saveVehicle(v);
                    return ResponseEntity.ok().body("{\"message\": \"Vehicle deactivated successfully\"}");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
