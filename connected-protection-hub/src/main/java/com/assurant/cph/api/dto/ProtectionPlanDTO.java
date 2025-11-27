package com.assurant.cph.api.dto;

import com.assurant.cph.core.domain.ProtectionPlan;
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
@Schema(description = "Protection Plan data transfer object")
public class ProtectionPlanDTO {

    @Schema(description = "Unique identifier of the protection plan")
    private UUID id;

    @NotBlank(message = "Plan name is required")
    @Schema(description = "Name of the protection plan", example = "Premium Electronics Protection", required = true)
    private String name;

    @Schema(description = "Description of the protection plan", example = "Comprehensive protection for electronic devices")
    private String description;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    @Schema(description = "Plan start date", required = true)
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @Schema(description = "Plan end date", required = true)
    private LocalDateTime endDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Premium amount must be positive")
    @Schema(description = "Premium amount", example = "99.99", required = true)
    private Double premiumAmount;

    @DecimalMin(value = "0.0", message = "Coverage limit must be positive")
    @Schema(description = "Maximum coverage amount", example = "1000.00")
    private Double coverageLimit;

    @DecimalMin(value = "0.0", message = "Deductible must be positive")
    @Schema(description = "Deductible amount", example = "50.00")
    private Double deductible;

    @Schema(description = "Plan status")
    private ProtectionPlan.PlanStatus status;

    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer ID", required = true)
    private UUID customerId;

    @NotNull(message = "Asset ID is required")
    @Schema(description = "Protected asset ID", required = true)
    private UUID assetId;

    @Schema(description = "Date when the plan was created")
    private LocalDateTime createdAt;

    @Schema(description = "Date when the plan was last updated")
    private LocalDateTime updatedAt;
}