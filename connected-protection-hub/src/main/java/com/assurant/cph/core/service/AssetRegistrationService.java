package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.ElectronicDevice;
import com.assurant.cph.core.domain.ProtectedAsset;
import com.assurant.cph.core.domain.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRegistrationService {

    ElectronicDevice registerElectronicDevice(ElectronicDevice electronicDevice);
    Vehicle registerVehicle(Vehicle vehicle);
    List<ProtectedAsset> getAllAssets();
    Optional<ProtectedAsset> getAssetById(UUID id);
    List<ProtectedAsset> getAssetsByCustomerId(UUID customerId);
    List<ProtectedAsset> getActiveAssetsByCustomerId(UUID customerId);
    List<ElectronicDevice> getAllElectronicDevices();
    List<Vehicle> getAllVehicles();
    ProtectedAsset updateAssetStatus(UUID id, ProtectedAsset.AssetStatus status);
    // void deleteAsset(UUID id);
}