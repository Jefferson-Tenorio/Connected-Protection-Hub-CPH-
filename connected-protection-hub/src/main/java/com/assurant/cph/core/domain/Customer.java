package com.assurant.cph.core.domain;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;

    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    @Pattern(regexp = "\\+?[0-9\\s\\-\\(\\)]{10,}", message = "Phone number should be valid")
    private String phoneNumber;

    @NotBlank(message = "Document number is required")
    @Column(unique = true, nullable = false)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProtectedAsset> protectedAssets;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProtectionPlan> protectionPlans;

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

    public enum DocumentType {
        CPF, CNPJ, PASSPORT, DRIVER_LICENSE, OTHER
    }

    @Embeddable
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }
}