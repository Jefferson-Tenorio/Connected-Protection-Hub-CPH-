package com.assurant.cph.api.controller;

import com.assurant.cph.api.dto.ClaimDTO;
import com.assurant.cph.core.domain.Claim;
import com.assurant.cph.core.mapper.ClaimMapper;
import com.assurant.cph.core.service.ClaimService;
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
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@Tag(name = "Claims", description = "Claim management APIs")
public class ClaimController {

    private final ClaimService claimService;
    private final ClaimMapper claimMapper;

    @PostMapping
    @Operation(summary = "Create a new claim", description = "Creates a new claim for a protection plan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Claim created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Protection plan not found"),
            @ApiResponse(responseCode = "409", description = "Protection plan is not active")
    })
    public ResponseEntity<ClaimDTO> createClaim(
            @Parameter(description = "Claim data")
            @Valid @RequestBody ClaimDTO claimDTO) {

        log.info("Creating new claim for protection plan: {}", claimDTO.getProtectionPlanId());

        Claim claim = claimMapper.toEntity(claimDTO);
        Claim createdClaim = claimService.createClaim(claim);
        ClaimDTO createdClaimDTO = claimMapper.toDTO(createdClaim);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdClaimDTO);
    }

    @GetMapping
    @Operation(summary = "Get all claims", description = "Retrieves a list of all claims")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all claims")
    public ResponseEntity<List<ClaimDTO>> getAllClaims() {
        log.info("Fetching all claims");

        List<ClaimDTO> claims = claimService.getAllClaims().stream()
                .map(claimMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(claims);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID", description = "Retrieves a specific claim by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Claim found"),
            @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimDTO> getClaimById(
            @Parameter(description = "Claim ID")
            @PathVariable UUID id) {

        log.info("Fetching claim by ID: {}", id);

        Optional<Claim> claim = claimService.getClaimById(id);
        return claim.map(c -> ResponseEntity.ok(claimMapper.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{claimNumber}")
    @Operation(summary = "Get claim by claim number", description = "Retrieves a claim by its unique claim number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Claim found"),
            @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<ClaimDTO> getClaimByClaimNumber(
            @Parameter(description = "Claim number")
            @PathVariable String claimNumber) {

        log.info("Fetching claim by number: {}", claimNumber);

        Optional<Claim> claim = claimService.getClaimByClaimNumber(claimNumber);
        return claim.map(c -> ResponseEntity.ok(claimMapper.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get claims by customer", description = "Retrieves all claims for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved claims")
    public ResponseEntity<List<ClaimDTO>> getClaimsByCustomerId(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        log.info("Fetching claims for customer: {}", customerId);

        List<ClaimDTO> claims = claimService.getClaimsByCustomerId(customerId).stream()
                .map(claimMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(claims);
    }

    @GetMapping("/protection-plan/{protectionPlanId}")
    @Operation(summary = "Get claims by protection plan", description = "Retrieves all claims for a specific protection plan")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved claims")
    public ResponseEntity<List<ClaimDTO>> getClaimsByProtectionPlanId(
            @Parameter(description = "Protection plan ID")
            @PathVariable UUID protectionPlanId) {

        log.info("Fetching claims for protection plan: {}", protectionPlanId);

        List<ClaimDTO> claims = claimService.getClaimsByProtectionPlanId(protectionPlanId).stream()
                .map(claimMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(claims);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get claims by status", description = "Retrieves all claims with a specific status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved claims")
    public ResponseEntity<List<ClaimDTO>> getClaimsByStatus(
            @Parameter(description = "Claim status")
            @PathVariable Claim.ClaimStatus status) {

        log.info("Fetching claims with status: {}", status);

        List<ClaimDTO> claims = claimService.getClaimsByStatus(status).stream()
                .map(claimMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(claims);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update claim status", description = "Updates the status of a specific claim")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Claim status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Claim not found"),
            @ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
    public ResponseEntity<ClaimDTO> updateClaimStatus(
            @Parameter(description = "Claim ID") @PathVariable UUID id,
            @Parameter(description = "New status") @RequestParam Claim.ClaimStatus status) {

        log.info("Updating claim status for ID: {} to {}", id, status);

        Claim updatedClaim = claimService.updateClaimStatus(id, status);
        ClaimDTO updatedClaimDTO = claimMapper.toDTO(updatedClaim);

        return ResponseEntity.ok(updatedClaimDTO);
    }

    @PostMapping("/{claimId}/assign-assessment/{assessmentId}")
    @Operation(summary = "Assign technical assessment to claim", description = "Assigns a technical assessment to a claim")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assessment assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Claim or assessment not found")
    })
    public ResponseEntity<ClaimDTO> assignAssessmentToClaim(
            @Parameter(description = "Claim ID") @PathVariable UUID claimId,
            @Parameter(description = "Technical assessment ID") @PathVariable UUID assessmentId) {

        log.info("Assigning assessment {} to claim {}", assessmentId, claimId);

        Claim updatedClaim = claimService.assignAssessment(claimId, assessmentId);
        ClaimDTO updatedClaimDTO = claimMapper.toDTO(updatedClaim);

        return ResponseEntity.ok(updatedClaimDTO);
    }
}