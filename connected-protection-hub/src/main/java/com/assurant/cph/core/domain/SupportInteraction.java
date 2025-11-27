package com.assurant.cph.core.domain;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "support_interactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Interaction reference is required")
    @Column(unique = true, nullable = false)
    private String interactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionChannel channel;

    @NotBlank(message = "Subject is required")
    @Column(nullable = false)
    private String subject;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionStatus status;

    private String agentName;
    private String resolutionNotes;

    private Integer satisfactionRating;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = InteractionStatus.OPEN;
        }
        if (interactionReference == null) {
            interactionReference = "SUP-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum InteractionType {
        INQUIRY, COMPLAINT, CLAIM_ASSISTANCE, TECHNICAL_SUPPORT, BILLING, FEEDBACK
    }

    public enum InteractionChannel {
        PHONE, EMAIL, CHAT, IN_PERSON, SOCIAL_MEDIA, SELF_SERVICE
    }

    public enum InteractionStatus {
        OPEN, IN_PROGRESS, RESOLVED, ESCALATED, CLOSED
    }
}