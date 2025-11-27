package com.assurant.cph.api.dto;

import com.assurant.cph.core.domain.Claim;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Claim data transfer object")
public class ClaimDTO {

    @Schema(description = "Unique identifier of the claim")
    private UUID id;

    @Schema(description = "Unique claim number")
    private String claimNumber;

    @NotNull(message = "Incident date is required")
    @PastOrPresent(message = "Incident date must be in the past or present")
    @Schema(description = "Date when the incident occurred", required = true)
    private LocalDateTime incidentDate;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    @Schema(description = "Detailed description of the claim", required = true)
    private String description;

    @Schema(description = "Current status of the claim")
    private Claim.ClaimStatus status;

    @NotNull(message = "Claim type is required")
    @Schema(description = "Type of claim", required = true)
    private Claim.ClaimType claimType;

    @DecimalMin(value = "0.0", message = "Claimed amount must be positive")
    @Schema(description = "Amount claimed by the customer")
    private Double claimedAmount;

    @DecimalMin(value = "0.0", message = "Approved amount must be positive")
    @Schema(description = "Amount approved by the assessor")
    private Double approvedAmount;

    @NotNull(message = "Protection plan ID is required")
    @Schema(description = "Protection plan ID", required = true)
    private UUID protectionPlanId;

    @Schema(description = "Date when the claim was created")
    private LocalDateTime createdAt;

    @Schema(description = "Date when the claim was last updated")
    private LocalDateTime updatedAt;
}