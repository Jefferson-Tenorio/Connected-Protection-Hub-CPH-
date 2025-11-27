package com.assurant.cph.core.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "protected_assets")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder // Mude para @SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProtectedAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Asset name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Serial number is required")
    @Column(unique = true, nullable = false)
    private String serialNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Purchase date is required")
    private LocalDateTime purchaseDate;

    @DecimalMin(value = "0.0", message = "Purchase value must be positive")
    private Double purchaseValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = AssetStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AssetStatus {
        ACTIVE, INACTIVE, UNDER_MAINTENANCE, DECOMMISSIONED, STOLEN, LOST
    }
}