package com.assurant.cph.core.domain;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Repair order number is required")
    @Column(unique = true, nullable = false)
    private String repairOrderNumber;

    @NotBlank(message = "Repair provider is required")
    @Column(nullable = false)
    private String repairProvider;

    @Email(message = "Provider contact should be a valid email")
    private String providerContact;

    @NotBlank(message = "Provider address is required")
    private String providerAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairStatus status;

    @NotBlank(message = "Repair description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String repairDescription;

    @Column(columnDefinition = "TEXT")
    private String diagnosedIssue;

    @Column(columnDefinition = "TEXT")
    private String partsReplaced;

    @DecimalMin(value = "0.0", message = "Repair cost must be positive")
    private Double repairCost;

    @NotNull(message = "Estimated completion date is required")
    @Future(message = "Estimated completion must be in the future")
    private LocalDateTime estimatedCompletion;

    private LocalDateTime actualCompletion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false, unique = true) // Added unique constraint
    private Claim claim;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Business fields addition
    private Double estimatedCost;
    private Integer warrantyDays;
    private String technicianNotes;
    private String customerFeedback;
    private Integer satisfactionRating; // 1-5 scale

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = RepairStatus.PENDING;
        }
        if (repairOrderNumber == null) {
            repairOrderNumber = "RO-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isOverdue() {
        return estimatedCompletion != null &&
                LocalDateTime.now().isAfter(estimatedCompletion) &&
                status != RepairStatus.COMPLETED &&
                status != RepairStatus.CANCELLED;
    }

    public boolean canBeCompleted() {
        return status != RepairStatus.COMPLETED &&
                status != RepairStatus.CANCELLED;
    }

    public void completeRepair(Double finalCost, String finalPartsReplaced) {
        this.status = RepairStatus.COMPLETED;
        this.actualCompletion = LocalDateTime.now();
        this.repairCost = finalCost;
        this.partsReplaced = finalPartsReplaced;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancelRepair(String reason) {
        this.status = RepairStatus.CANCELLED;
        if (reason != null && !reason.trim().isEmpty()) {
            this.technicianNotes = (this.technicianNotes != null ? this.technicianNotes + "\n" : "") +
                    "Cancellation: " + reason;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public Long getDaysInRepair() {
        if (actualCompletion != null && createdAt != null) {
            return java.time.Duration.between(createdAt, actualCompletion).toDays();
        } else if (createdAt != null) {
            return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
        }
        return 0L;
    }

    public enum RepairStatus {
        PENDING("Pendente"),
        DIAGNOSIS("Em Diagnóstico"),
        WAITING_PARTS("Aguardando Peças"),
        IN_PROGRESS("Em Andamento"),
        COMPLETED("Concluído"),
        CANCELLED("Cancelado");

        private final String description;

        RepairStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Custom builder for business logic
    public static RepairOrderBuilder builder() {
        return new CustomRepairOrderBuilder();
    }

    private static class CustomRepairOrderBuilder extends RepairOrderBuilder {
        @Override
        public RepairOrder build() {
            RepairOrder repairOrder = super.build();

            // Ensure business rules
            if (repairOrder.getStatus() == null) {
                repairOrder.setStatus(RepairStatus.PENDING);
            }

            if (repairOrder.getRepairOrderNumber() == null) {
                repairOrder.setRepairOrderNumber("RO-" + System.currentTimeMillis());
            }

            return repairOrder;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "RepairOrder{id=%s, number='%s', provider='%s', status=%s, claim=%s}",
                id, repairOrderNumber, repairProvider, status,
                claim != null ? claim.getId() : "null"
        );
    }
}