package com.assurant.cph.core.domain;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "protection_plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtectionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Plan name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @DecimalMin(value = "0.0", message = "Premium amount must be positive")
    @Column(nullable = false)
    private Double premiumAmount;

    @DecimalMin(value = "0.0", message = "Coverage limit must be positive")
    private Double coverageLimit;

    private Double deductible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private ProtectedAsset protectedAsset;

    @OneToMany(mappedBy = "protectionPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Claim> claims;

    @OneToMany(mappedBy = "protectionPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentRecord> paymentRecords;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = PlanStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PlanStatus {
        ACTIVE, INACTIVE, EXPIRED, CANCELLED, SUSPENDED
    }
}