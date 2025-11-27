package com.assurant.cph.core.domain;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "technical_assessments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Assessor name is required")
    @Column(nullable = false)
    private String assessorName;

    @NotNull(message = "Assessment date is required")
    private LocalDateTime assessmentDate;

    @NotBlank(message = "Findings are required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String findings;

    private String recommendations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentResult result;

    @DecimalMin(value = "0.0", message = "Estimated repair cost must be positive")
    private Double estimatedRepairCost;

    private Boolean coveredByWarranty;
    private Boolean coveredByInsurance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AssessmentResult {
        REPAIRABLE, BEYOND_REPAIR, REPLACEMENT_NEEDED, MINOR_ISSUE, MAJOR_ISSUE
    }
}