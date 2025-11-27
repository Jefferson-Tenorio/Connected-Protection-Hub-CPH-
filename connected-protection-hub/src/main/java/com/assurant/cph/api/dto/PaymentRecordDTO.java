package com.assurant.cph.api.dto;

import com.assurant.cph.core.domain.PaymentRecord;
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
@Schema(description = "Payment Record data transfer object")
public class PaymentRecordDTO {

    @Schema(description = "Unique identifier of the payment record")
    private UUID id;

    @NotBlank(message = "Payment reference is required")
    @Schema(description = "Unique payment reference number", example = "PAY-20240120-001", required = true)
    private String paymentReference;

    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be positive")
    @Schema(description = "Payment amount", example = "299.99", required = true)
    private Double amount;

    @NotNull(message = "Payment date is required")
    @Schema(description = "Date and time of the payment", required = true)
    private LocalDateTime paymentDate;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Payment method used", required = true)
    private PaymentRecord.PaymentMethod paymentMethod;

    @Schema(description = "Current payment status")
    private PaymentRecord.PaymentStatus status;

    @Schema(description = "Transaction ID from payment gateway", example = "TXN123456789")
    private String transactionId;

    @Schema(description = "Payer information", example = "João Silva - Cartão final 1234")
    private String payerInfo;

    @Schema(description = "Additional payment details")
    private String paymentDetails;

    @NotNull(message = "Protection plan ID is required")
    @Schema(description = "Protection plan ID associated with this payment", required = true)
    private UUID protectionPlanId;

    @Schema(description = "Date when the payment record was created")
    private LocalDateTime createdAt;

    @Schema(description = "Date when the payment record was last updated")
    private LocalDateTime updatedAt;

    // Helper methods for business logic in DTO layer
    public boolean isRefundable() {
        return status == PaymentRecord.PaymentStatus.COMPLETED &&
                paymentDate != null &&
                paymentDate.isAfter(LocalDateTime.now().minusDays(30));
    }

    public String getStatusDescription() {
        return status != null ? status.getDescription() : "Desconhecido";
    }

    public String getMethodDescription() {
        return paymentMethod != null ? paymentMethod.getDescription() : "Desconhecido";
    }
}