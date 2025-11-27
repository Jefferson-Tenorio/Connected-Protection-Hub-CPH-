package com.assurant.cph.api.controller;

import com.assurant.cph.api.dto.ProtectionPlanDTO;
import com.assurant.cph.core.domain.ProtectionPlan;
import com.assurant.cph.core.mapper.ProtectionPlanMapper;
import com.assurant.cph.core.service.ProtectionPlanService;
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
@RequestMapping("/api/v1/protection-plans")
@RequiredArgsConstructor
@Tag(name = "Protection Plans", description = "Protection plan management APIs")
public class ProtectionPlanController {

    private final ProtectionPlanService protectionPlanService;
    private final ProtectionPlanMapper protectionPlanMapper;

    @PostMapping
    @Operation(summary = "Create a new protection plan", description = "Creates a new protection plan for a customer and asset")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Protection plan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Customer or asset not found")
    })
    public ResponseEntity<ProtectionPlanDTO> createProtectionPlan(
            @Parameter(description = "Protection plan data")
            @Valid @RequestBody ProtectionPlanDTO protectionPlanDTO) {

        log.info("Creating new protection plan for customer: {}", protectionPlanDTO.getCustomerId());

        ProtectionPlan protectionPlan = protectionPlanMapper.toEntity(protectionPlanDTO);
        ProtectionPlan createdPlan = protectionPlanService.createProtectionPlan(protectionPlan);
        ProtectionPlanDTO createdPlanDTO = protectionPlanMapper.toDTO(createdPlan);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlanDTO);
    }

    @GetMapping
    @Operation(summary = "Get all protection plans", description = "Retrieves a list of all protection plans")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all protection plans")
    public ResponseEntity<List<ProtectionPlanDTO>> getAllProtectionPlans() {
        log.info("Fetching all protection plans");

        List<ProtectionPlanDTO> plans = protectionPlanService.getAllProtectionPlans().stream()
                .map(protectionPlanMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get protection plan by ID", description = "Retrieves a specific protection plan by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Protection plan found"),
            @ApiResponse(responseCode = "404", description = "Protection plan not found")
    })
    public ResponseEntity<ProtectionPlanDTO> getProtectionPlanById(
            @Parameter(description = "Protection plan ID")
            @PathVariable UUID id) {

        log.info("Fetching protection plan by ID: {}", id);

        Optional<ProtectionPlan> plan = protectionPlanService.getProtectionPlanById(id);
        return plan.map(p -> ResponseEntity.ok(protectionPlanMapper.toDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get protection plans by customer", description = "Retrieves all protection plans for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved protection plans")
    public ResponseEntity<List<ProtectionPlanDTO>> getProtectionPlansByCustomerId(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        log.info("Fetching protection plans for customer: {}", customerId);

        List<ProtectionPlanDTO> plans = protectionPlanService.getProtectionPlansByCustomerId(customerId).stream()
                .map(protectionPlanMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(plans);
    }

    @GetMapping("/customer/{customerId}/active")
    @Operation(summary = "Get active protection plans by customer", description = "Retrieves active protection plans for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved active protection plans")
    public ResponseEntity<List<ProtectionPlanDTO>> getActiveProtectionPlansByCustomerId(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        log.info("Fetching active protection plans for customer: {}", customerId);

        List<ProtectionPlanDTO> plans = protectionPlanService.getActiveProtectionPlansByCustomerId(customerId).stream()
                .map(protectionPlanMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(plans);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update protection plan", description = "Updates an existing protection plan's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Protection plan updated successfully"),
            @ApiResponse(responseCode = "404", description = "Protection plan not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ProtectionPlanDTO> updateProtectionPlan(
            @Parameter(description = "Protection plan ID") @PathVariable UUID id,
            @Parameter(description = "Updated protection plan data") @Valid @RequestBody ProtectionPlanDTO protectionPlanDTO) {

        log.info("Updating protection plan with ID: {}", id);

        ProtectionPlan protectionPlan = protectionPlanMapper.toEntity(protectionPlanDTO);
        ProtectionPlan updatedPlan = protectionPlanService.updateProtectionPlan(id, protectionPlan);
        ProtectionPlanDTO updatedPlanDTO = protectionPlanMapper.toDTO(updatedPlan);

        return ResponseEntity.ok(updatedPlanDTO);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel protection plan", description = "Cancels an active protection plan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Protection plan cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Protection plan not found"),
            @ApiResponse(responseCode = "409", description = "Protection plan is not active")
    })
    public ResponseEntity<Void> cancelProtectionPlan(
            @Parameter(description = "Protection plan ID")
            @PathVariable UUID id) {

        log.info("Cancelling protection plan with ID: {}", id);
        protectionPlanService.cancelProtectionPlan(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/renew")
    @Operation(summary = "Renew protection plan", description = "Renews a protection plan for specified number of months")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Protection plan renewed successfully"),
            @ApiResponse(responseCode = "404", description = "Protection plan not found"),
            @ApiResponse(responseCode = "409", description = "Protection plan cannot be renewed")
    })
    public ResponseEntity<Void> renewProtectionPlan(
            @Parameter(description = "Protection plan ID") @PathVariable UUID id,
            @Parameter(description = "Number of months to renew") @RequestParam int months) {

        log.info("Renewing protection plan with ID: {} for {} months", id, months);
        protectionPlanService.renewProtectionPlan(id, months);
        return ResponseEntity.ok().build();
    }
}