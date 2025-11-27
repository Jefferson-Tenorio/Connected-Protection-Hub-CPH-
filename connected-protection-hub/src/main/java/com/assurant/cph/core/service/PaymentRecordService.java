package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.PaymentRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRecordService {

    // Basic CRUD operations
    PaymentRecord processPayment(PaymentRecord paymentRecord);
    List<PaymentRecord> getAllPayments();
    Optional<PaymentRecord> getPaymentById(UUID id);
    PaymentRecord updatePayment(UUID id, PaymentRecord paymentDetails);
    void deletePayment(UUID id);

    // Search and filter operations
    Optional<PaymentRecord> getPaymentByReference(String paymentReference);
    Optional<PaymentRecord> getPaymentByTransactionId(String transactionId);
    List<PaymentRecord> getPaymentsByProtectionPlanId(UUID protectionPlanId);
    List<PaymentRecord> getPaymentsByCustomerId(UUID customerId);
    List<PaymentRecord> getPaymentsByStatus(PaymentRecord.PaymentStatus status);
    List<PaymentRecord> getPaymentsByMethod(PaymentRecord.PaymentMethod paymentMethod);
    List<PaymentRecord> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Business operations
    PaymentRecord updatePaymentStatus(UUID id, PaymentRecord.PaymentStatus status);
    PaymentRecord processRefund(UUID id);
    PaymentRecord markAsCompleted(UUID id, String transactionId);
    PaymentRecord markAsFailed(UUID id, String failureReason);

    // Analytics and reporting
    Double getTotalPaidAmountByCustomer(UUID customerId);
    Map<String, Object> getPaymentStatistics(UUID customerId);
    Map<String, Long> getPaymentCountByStatus();
    Map<String, Double> getRevenueByPaymentMethod();

    // Validation operations
    boolean paymentExists(UUID id);
    boolean isPaymentRefundable(UUID id);
    boolean canRetryPayment(UUID id);

    // Batch operations
    List<PaymentRecord> processBatchPayments(List<PaymentRecord> payments);
    void cancelExpiredPendingPayments();
}