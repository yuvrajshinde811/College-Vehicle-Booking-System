package com.yuvraj.boot.service;

import com.yuvraj.boot.entity.Driver;
import com.yuvraj.boot.entity.Vehicle;
import com.yuvraj.boot.repository.DriverRepository;
import com.yuvraj.boot.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class FleetService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public FleetService(VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(int id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(int id) {
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> getAvailableVehicles(int minCapacity, Timestamp start, Timestamp end) {
        return vehicleRepository.findAvailableVehicles(minCapacity, start, end);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(int id) {
        return driverRepository.findById(id);
    }

    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public void deleteDriver(int id) {
        driverRepository.deleteById(id);
    }

    public List<Driver> getAvailableDrivers(Timestamp start, Timestamp end) {
        return driverRepository.findAvailableDrivers(start, end);
    }
}
