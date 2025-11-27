package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.ElectronicDevice;
import com.assurant.cph.core.domain.ProtectedAsset;
import com.assurant.cph.core.domain.Vehicle;
import com.assurant.cph.core.repository.ElectronicDeviceRepository;
import com.assurant.cph.core.repository.ProtectedAssetRepository;
import com.assurant.cph.core.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AssetRegistrationServiceImpl implements AssetRegistrationService {

    private final ProtectedAssetRepository protectedAssetRepository;
    private final ElectronicDeviceRepository electronicDeviceRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerService customerService;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "assets", allEntries = true),
            @CacheEvict(value = "customerAssets", allEntries = true),
            @CacheEvict(value = "electronicDevices", allEntries = true)
    })
    public ElectronicDevice registerElectronicDevice(ElectronicDevice electronicDevice) {
        log.info("Registering electronic device: {}", electronicDevice.getSerialNumber());

        // Validate customer exists
        if (!customerService.customerExists(electronicDevice.getCustomer().getId())) {
            throw new IllegalArgumentException("Customer not found with ID: " + electronicDevice.getCustomer().getId());
        }

        // Check if serial number already exists
        if (protectedAssetRepository.findBySerialNumber(electronicDevice.getSerialNumber()).isPresent()) {
            throw new IllegalArgumentException("Electronic device with serial number " + electronicDevice.getSerialNumber() + " already exists");
        }

        return electronicDeviceRepository.save(electronicDevice);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "assets", allEntries = true),
            @CacheEvict(value = "customerAssets", allEntries = true),
            @CacheEvict(value = "vehicles", allEntries = true)
    })
    public Vehicle registerVehicle(Vehicle vehicle) {
        log.info("Registering vehicle: {}", vehicle.getLicensePlate());

        // Validate customer exists
        if (!customerService.customerExists(vehicle.getCustomer().getId())) {
            throw new IllegalArgumentException("Customer not found with ID: " + vehicle.getCustomer().getId());
        }

        // Check if license plate already exists
        if (vehicleRepository.findByLicensePlate(vehicle.getLicensePlate()).isPresent()) {
            throw new IllegalArgumentException("Vehicle with license plate " + vehicle.getLicensePlate() + " already exists");
        }

        return vehicleRepository.save(vehicle);
    }

    @Override
    @Cacheable(value = "assets")
    @Transactional(readOnly = true)
    public List<ProtectedAsset> getAllAssets() {
        log.info("Fetching all protected assets");
        return protectedAssetRepository.findAll();
    }

    @Override
    @Cacheable(value = "asset", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ProtectedAsset> getAssetById(UUID id) {
        log.info("Fetching asset by ID: {}", id);
        return protectedAssetRepository.findById(id);
    }

    @Override
    @Cacheable(value = "customerAssets", key = "#customerId")
    @Transactional(readOnly = true)
    public List<ProtectedAsset> getAssetsByCustomerId(UUID customerId) {
        log.info("Fetching assets for customer: {}", customerId);
        return protectedAssetRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectedAsset> getActiveAssetsByCustomerId(UUID customerId) {
        log.info("Fetching active assets for customer: {}", customerId);
        return protectedAssetRepository.findActiveAssetsByCustomerId(customerId);
    }

    @Override
    @Cacheable(value = "electronicDevices")
    @Transactional(readOnly = true)
    public List<ElectronicDevice> getAllElectronicDevices() {
        log.info("Fetching all electronic devices");
        return electronicDeviceRepository.findAll();
    }

    @Override
    @Cacheable(value = "vehicles")
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        log.info("Fetching all vehicles");
        return vehicleRepository.findAll();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "assets", allEntries = true),
            @CacheEvict(value = "asset", key = "#id"),
            @CacheEvict(value = "customerAssets", allEntries = true)
    })
    public ProtectedAsset updateAssetStatus(UUID id, ProtectedAsset.AssetStatus status) {
        log.info("Updating asset status for ID: {} to {}", id, status);

        ProtectedAsset asset = protectedAssetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with ID: " + id));

        asset.setStatus(status);
        return protectedAssetRepository.save(asset);
    }
}