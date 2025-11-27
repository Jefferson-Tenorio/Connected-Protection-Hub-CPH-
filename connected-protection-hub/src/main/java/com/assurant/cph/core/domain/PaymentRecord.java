package com.assurant.cph.core.domain;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Payment reference is required")
    @Column(unique = true, nullable = false)
    private String paymentReference;

    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be positive")
    @Column(nullable = false)
    private Double amount;

    @NotNull(message = "Payment date is required")
    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(unique = true)
    private String transactionId;

    private String payerInfo;

    @Column(columnDefinition = "TEXT")
    private String paymentDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protection_plan_id", nullable = false)
    private ProtectionPlan protectionPlan;

    @OneToOne(mappedBy = "paymentRecord", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Claim claim;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Set default values if not provided
        if (status == null) {
            status = PaymentStatus.PENDING;
        }

        if (paymentReference == null) {
            paymentReference = "PAY-" + System.currentTimeMillis();
        }

        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isRefundable() {
        return status == PaymentStatus.COMPLETED &&
                paymentDate.isAfter(LocalDateTime.now().minusDays(30));
    }

    public boolean canRetry() {
        return status == PaymentStatus.FAILED || status == PaymentStatus.PENDING;
    }

    public void markAsCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        if (failureReason != null && !failureReason.isEmpty()) {
            this.paymentDetails = (this.paymentDetails != null ? this.paymentDetails + "\n" : "") +
                    "Failure: " + failureReason;
        }
        this.updatedAt = LocalDateTime.now();
    }

    // Enum for Payment Methods
    public enum PaymentMethod {
        CREDIT_CARD("Cartão de Crédito"),
        DEBIT_CARD("Cartão de Débito"),
        BANK_TRANSFER("Transferência Bancária"),
        PIX("PIX"),
        DIGITAL_WALLET("Carteira Digital"),
        CASH("Dinheiro"),
        CHECK("Cheque"),
        BANK_SLIP("Boleto Bancário"),
        OTHER("Outro");

        private final String description;

        PaymentMethod(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Enum for Payment Status
    public enum PaymentStatus {
        PENDING("Pendente"),
        COMPLETED("Concluído"),
        FAILED("Falhou"),
        REFUNDED("Reembolsado"),
        CANCELLED("Cancelado"),
        IN_PROCESS("Em Processamento"),
        PARTIALLY_REFUNDED("Parcialmente Reembolsado"),
        CHARGEBACK("Chargeback"),
        EXPIRED("Expirado");

        private final String description;

        PaymentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public boolean isTerminal() {
            return this == COMPLETED ||
                    this == REFUNDED ||
                    this == CANCELLED ||
                    this == CHARGEBACK ||
                    this == EXPIRED;
        }
    }

    // Builder pattern with customizations
    public static PaymentRecordBuilder builder() {
        return new CustomPaymentRecordBuilder();
    }

    private static class CustomPaymentRecordBuilder extends PaymentRecordBuilder {
        @Override
        public PaymentRecord build() {
            PaymentRecord payment = super.build();

            // Ensure required fields are set
            if (payment.getStatus() == null) {
                payment.setStatus(PaymentStatus.PENDING);
            }

            if (payment.getPaymentDate() == null) {
                payment.setPaymentDate(LocalDateTime.now());
            }

            return payment;
        }
    }

    // toString method for better logging
    @Override
    public String toString() {
        return String.format(
                "PaymentRecord{id=%s, reference='%s', amount=%.2f, status=%s, method=%s}",
                id, paymentReference, amount, status, paymentMethod
        );
    }

    // Utility methods for business logic
    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean requiresAction() {
        return status == PaymentStatus.PENDING ||
                status == PaymentStatus.IN_PROCESS ||
                status == PaymentStatus.FAILED;
    }

    public String getStatusDescription() {
        return status != null ? status.getDescription() : "Desconhecido";
    }

    public String getMethodDescription() {
        return paymentMethod != null ? paymentMethod.getDescription() : "Desconhecido";
    }
}