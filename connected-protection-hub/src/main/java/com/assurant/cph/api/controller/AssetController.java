package com.assurant.cph.api.controller;

import com.assurant.cph.api.dto.AssetDTO;
import com.assurant.cph.core.domain.ElectronicDevice;
import com.assurant.cph.core.domain.ProtectedAsset;
import com.assurant.cph.core.domain.Vehicle;
import com.assurant.cph.core.service.AssetRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Protected asset management APIs")
public class AssetController {

    private final AssetRegistrationService assetRegistrationService;

    @PostMapping("/electronic-devices")
    @Operation(summary = "Register electronic device", description = "Registers a new electronic device as a protected asset")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Electronic device registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Serial number already exists")
    })
    public ResponseEntity<ElectronicDevice> registerElectronicDevice(
            @Parameter(description = "Electronic device data")
            @Valid @RequestBody ElectronicDevice electronicDevice) {

        log.info("Registering electronic device: {}", electronicDevice.getSerialNumber());

        ElectronicDevice createdDevice = assetRegistrationService.registerElectronicDevice(electronicDevice);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
    }

    @PostMapping("/vehicles")
    @Operation(summary = "Register vehicle", description = "Registers a new vehicle as a protected asset")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "License plate already exists")
    })
    public ResponseEntity<Vehicle> registerVehicle(
            @Parameter(description = "Vehicle data")
            @Valid @RequestBody Vehicle vehicle) {

        log.info("Registering vehicle: {}", vehicle.getLicensePlate());

        Vehicle createdVehicle = assetRegistrationService.registerVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }

    @GetMapping
    @Operation(summary = "Get all assets", description = "Retrieves a list of all protected assets")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all assets")
    public ResponseEntity<List<ProtectedAsset>> getAllAssets() {
        log.info("Fetching all protected assets");
        List<ProtectedAsset> assets = assetRegistrationService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get asset by ID", description = "Retrieves a specific asset by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset found"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ProtectedAsset> getAssetById(
            @Parameter(description = "Asset ID")
            @PathVariable UUID id) {

        log.info("Fetching asset by ID: {}", id);

        Optional<ProtectedAsset> asset = assetRegistrationService.getAssetById(id);
        return asset.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get assets by customer", description = "Retrieves all assets for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved assets")
    public ResponseEntity<List<ProtectedAsset>> getAssetsByCustomerId(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        log.info("Fetching assets for customer: {}", customerId);

        List<ProtectedAsset> assets = assetRegistrationService.getAssetsByCustomerId(customerId);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/customer/{customerId}/active")
    @Operation(summary = "Get active assets by customer", description = "Retrieves active assets for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active assets")
    public ResponseEntity<List<ProtectedAsset>> getActiveAssetsByCustomerId(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        log.info("Fetching active assets for customer: {}", customerId);

        List<ProtectedAsset> assets = assetRegistrationService.getActiveAssetsByCustomerId(customerId);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/electronic-devices")
    @Operation(summary = "Get all electronic devices", description = "Retrieves all electronic devices")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved electronic devices")
    public ResponseEntity<List<ElectronicDevice>> getAllElectronicDevices() {
        log.info("Fetching all electronic devices");
        List<ElectronicDevice> devices = assetRegistrationService.getAllElectronicDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/vehicles")
    @Operation(summary = "Get all vehicles", description = "Retrieves all vehicles")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicles")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        log.info("Fetching all vehicles");
        List<Vehicle> vehicles = assetRegistrationService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update asset status", description = "Updates the status of a specific asset")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<ProtectedAsset> updateAssetStatus(
            @Parameter(description = "Asset ID") @PathVariable UUID id,
            @Parameter(description = "New status") @RequestParam ProtectedAsset.AssetStatus status) {

        log.info("Updating asset status for ID: {} to {}", id, status);

        ProtectedAsset updatedAsset = assetRegistrationService.updateAssetStatus(id, status);
        return ResponseEntity.ok(updatedAsset);
    }

    //@DeleteMapping("/{id}")
    //@Operation(summary = "Delete asset", description = "Deletes an asset by its unique identifier")
    //@ApiResponses({
    //        @ApiResponse(responseCode = "204", description = "Asset deleted successfully"),
    //        @ApiResponse(responseCode = "404", description = "Asset not found"),
    //        @ApiResponse(responseCode = "409", description = "Asset has active protection plans")
    //})
    //public ResponseEntity<Void> deleteAsset(
    //        @Parameter(description = "Asset ID")
    //        @PathVariable UUID id) {

    //    log.info("Deleting asset with ID: {}", id);
    //    assetRegistrationService.deleteAsset(id);
    //    return ResponseEntity.noContent().build();
    //}
}