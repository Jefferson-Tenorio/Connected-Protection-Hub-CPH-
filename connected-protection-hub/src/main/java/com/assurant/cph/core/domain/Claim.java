package com.assurant.cph.core.domain;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Claim number is required")
    @Column(unique = true, nullable = false)
    private String claimNumber;

    @NotNull(message = "Incident date is required")
    private LocalDateTime incidentDate;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimType claimType;

    @DecimalMin(value = "0.0", message = "Claim amount must be positive")
    private Double claimedAmount;

    private Double approvedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protection_plan_id", nullable = false)
    private ProtectionPlan protectionPlan;

    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RepairOrder repairOrder;

    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TechnicalAssessment technicalAssessment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ClaimStatus.SUBMITTED;
        }
        if (claimNumber == null) {
            claimNumber = "CLM-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ClaimStatus {
        SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, IN_REPAIR, COMPLETED, CANCELLED
    }

    public enum ClaimType {
        THEFT, DAMAGE, MALFUNCTION, LOSS, ACCIDENT, OTHER
    }
}