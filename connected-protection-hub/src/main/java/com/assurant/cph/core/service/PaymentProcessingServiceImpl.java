package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.PaymentRecord;
import com.assurant.cph.core.domain.ProtectionPlan;
import com.assurant.cph.core.repository.PaymentRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentProcessingServiceImpl implements PaymentRecordService {

    private final PaymentRecordRepository paymentRecordRepository;
    private final ProtectionPlanService protectionPlanService;
    private final CustomerService customerService;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public PaymentRecord processPayment(PaymentRecord paymentRecord) {
        log.info("Processing payment for protection plan: {}", paymentRecord.getProtectionPlan().getId());

        // Validate protection plan exists and is active
        ProtectionPlan protectionPlan = protectionPlanService.getProtectionPlanById(paymentRecord.getProtectionPlan().getId())
                .orElseThrow(() -> new IllegalArgumentException("Protection plan not found with ID: " + paymentRecord.getProtectionPlan().getId()));

        if (protectionPlan.getStatus() != ProtectionPlan.PlanStatus.ACTIVE &&
                protectionPlan.getStatus() != ProtectionPlan.PlanStatus.EXPIRED) {
            throw new IllegalStateException("Cannot process payment for protection plan with status: " + protectionPlan.getStatus());
        }

        // Validate payment amount matches plan premium (with tolerance for fees)
        double expectedAmount = protectionPlan.getPremiumAmount();
        double paymentAmount = paymentRecord.getAmount();
        double tolerance = 0.01; // 1% tolerance

        if (Math.abs(paymentAmount - expectedAmount) > (expectedAmount * tolerance)) {
            throw new IllegalArgumentException(
                    String.format("Payment amount %.2f does not match protection plan premium %.2f",
                            paymentAmount, expectedAmount)
            );
        }

        // Set default values if not provided
        if (paymentRecord.getPaymentDate() == null) {
            paymentRecord.setPaymentDate(LocalDateTime.now());
        }

        if (paymentRecord.getStatus() == null) {
            paymentRecord.setStatus(PaymentRecord.PaymentStatus.PENDING);
        }

        // Generate payment reference if not provided
        if (paymentRecord.getPaymentReference() == null) {
            paymentRecord.setPaymentReference(generatePaymentReference());
        }

        // Validate unique payment reference
        if (paymentRecordRepository.findByPaymentReference(paymentRecord.getPaymentReference()).isPresent()) {
            throw new IllegalArgumentException("Payment reference already exists: " + paymentRecord.getPaymentReference());
        }

        PaymentRecord savedPayment = paymentRecordRepository.save(paymentRecord);
        log.info("Payment processed successfully with reference: {}", savedPayment.getPaymentReference());

        return savedPayment;
    }

    @Override
    @Cacheable(value = "payments")
    @Transactional(readOnly = true)
    public List<PaymentRecord> getAllPayments() {
        log.info("Fetching all payment records");
        return paymentRecordRepository.findAll();
    }

    @Override
    @Cacheable(value = "payment", key = "#id")
    @Transactional(readOnly = true)
    public Optional<PaymentRecord> getPaymentById(UUID id) {
        log.info("Fetching payment by ID: {}", id);
        return paymentRecordRepository.findById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "payment", key = "#id"),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public PaymentRecord updatePayment(UUID id, PaymentRecord paymentDetails) {
        log.info("Updating payment with ID: {}", id);

        PaymentRecord existingPayment = paymentRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found with ID: " + id));

        // Only allow updates to certain fields
        if (paymentDetails.getPayerInfo() != null) {
            existingPayment.setPayerInfo(paymentDetails.getPayerInfo());
        }

        if (paymentDetails.getPaymentDetails() != null) {
            existingPayment.setPaymentDetails(paymentDetails.getPaymentDetails());
        }

        if (paymentDetails.getTransactionId() != null) {
            // Validate unique transaction ID
            if (paymentRecordRepository.findByTransactionId(paymentDetails.getTransactionId())
                    .filter(p -> !p.getId().equals(id)).isPresent()) {
                throw new IllegalArgumentException("Transaction ID already exists: " + paymentDetails.getTransactionId());
            }
            existingPayment.setTransactionId(paymentDetails.getTransactionId());
        }

        return paymentRecordRepository.save(existingPayment);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "payment", key = "#id"),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public void deletePayment(UUID id) {
        log.info("Deleting payment with ID: {}", id);

        PaymentRecord payment = paymentRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found with ID: " + id));

        // Only allow deletion of pending or failed payments
        if (payment.getStatus() == PaymentRecord.PaymentStatus.COMPLETED ||
                payment.getStatus() == PaymentRecord.PaymentStatus.REFUNDED) {
            throw new IllegalStateException("Cannot delete completed or refunded payments");
        }

        paymentRecordRepository.delete(payment);
        log.info("Payment deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentRecord> getPaymentByReference(String paymentReference) {
        log.info("Fetching payment by reference: {}", paymentReference);
        return paymentRecordRepository.findByPaymentReference(paymentReference);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentRecord> getPaymentByTransactionId(String transactionId) {
        log.info("Fetching payment by transaction ID: {}", transactionId);
        return paymentRecordRepository.findByTransactionId(transactionId);
    }

    @Override
    @Cacheable(value = "planPayments", key = "#protectionPlanId")
    @Transactional(readOnly = true)
    public List<PaymentRecord> getPaymentsByProtectionPlanId(UUID protectionPlanId) {
        log.info("Fetching payments for protection plan: {}", protectionPlanId);
        return paymentRecordRepository.findByProtectionPlanId(protectionPlanId);
    }

    @Override
    @Cacheable(value = "customerPayments", key = "#customerId")
    @Transactional(readOnly = true)
    public List<PaymentRecord> getPaymentsByCustomerId(UUID customerId) {
        log.info("Fetching payments for customer: {}", customerId);

        // Validate customer exists
        if (!customerService.customerExists(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }

        return paymentRecordRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> getPaymentsByStatus(PaymentRecord.PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        return paymentRecordRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> getPaymentsByMethod(PaymentRecord.PaymentMethod paymentMethod) {
        log.info("Fetching payments by method: {}", paymentMethod);
        return paymentRecordRepository.findByPaymentMethod(paymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching payments between {} and {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return paymentRecordRepository.findAll().stream()
                .filter(payment -> !payment.getPaymentDate().isBefore(startDate) &&
                        !payment.getPaymentDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "payment", key = "#id"),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public PaymentRecord updatePaymentStatus(UUID id, PaymentRecord.PaymentStatus status) {
        log.info("Updating payment status for ID: {} to {}", id, status);

        PaymentRecord payment = paymentRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found with ID: " + id));

        // Validate status transition
        validatePaymentStatusTransition(payment.getStatus(), status);

        payment.setStatus(status);

        // If payment is completed, update the protection plan accordingly
        if (status == PaymentRecord.PaymentStatus.COMPLETED) {
            updateProtectionPlanAfterPayment(payment.getProtectionPlan());
        }

        PaymentRecord updatedPayment = paymentRecordRepository.save(payment);
        log.info("Payment status updated successfully: {} -> {}", id, status);

        return updatedPayment;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "payment", key = "#id"),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public PaymentRecord processRefund(UUID id) {
        log.info("Processing refund for payment: {}", id);

        PaymentRecord payment = paymentRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found with ID: " + id));

        // Validate if refund is possible
        if (!payment.isRefundable()) {
            throw new IllegalStateException("Payment is not refundable. Status: " + payment.getStatus() +
                    ", Payment Date: " + payment.getPaymentDate());
        }

        payment.setStatus(PaymentRecord.PaymentStatus.REFUNDED);

        // Update protection plan status if needed
        handleProtectionPlanAfterRefund(payment.getProtectionPlan());

        PaymentRecord refundedPayment = paymentRecordRepository.save(payment);
        log.info("Payment refunded successfully: {}", refundedPayment.getPaymentReference());

        return refundedPayment;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "payment", key = "#id"),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public PaymentRecord markAsCompleted(UUID id, String transactionId) {
        log.info("Marking payment as completed: {} with transaction ID: {}", id, transactionId);

        PaymentRecord payment = paymentRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found with ID: " + id));

        payment.markAsCompleted(transactionId);
        updateProtectionPlanAfterPayment(payment.getProtectionPlan());

        PaymentRecord completedPayment = paymentRecordRepository.save(payment);
        log.info("Payment marked as completed: {}", id);

        return completedPayment;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "payment", key = "#id"),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public PaymentRecord markAsFailed(UUID id, String failureReason) {
        log.info("Marking payment as failed: {} - Reason: {}", id, failureReason);

        PaymentRecord payment = paymentRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found with ID: " + id));

        payment.markAsFailed(failureReason);

        PaymentRecord failedPayment = paymentRecordRepository.save(payment);
        log.info("Payment marked as failed: {}", id);

        return failedPayment;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalPaidAmountByCustomer(UUID customerId) {
        log.info("Calculating total paid amount for customer: {}", customerId);

        // Validate customer exists
        if (!customerService.customerExists(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }

        Double total = paymentRecordRepository.getTotalPaidAmountByCustomer(customerId);
        return total != null ? total : 0.0;
    }

    @Override
    @Cacheable(value = "paymentStats", key = "#customerId")
    @Transactional(readOnly = true)
    public Map<String, Object> getPaymentStatistics(UUID customerId) {
        log.info("Generating payment statistics for customer: {}", customerId);

        List<PaymentRecord> customerPayments = getPaymentsByCustomerId(customerId);

        double totalPaid = customerPayments.stream()
                .filter(p -> p.getStatus() == PaymentRecord.PaymentStatus.COMPLETED)
                .mapToDouble(PaymentRecord::getAmount)
                .sum();

        long completedPayments = customerPayments.stream()
                .filter(p -> p.getStatus() == PaymentRecord.PaymentStatus.COMPLETED)
                .count();

        long pendingPayments = customerPayments.stream()
                .filter(p -> p.getStatus() == PaymentRecord.PaymentStatus.PENDING)
                .count();

        double averagePayment = completedPayments > 0 ? totalPaid / completedPayments : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("customerId", customerId);
        stats.put("totalPaid", totalPaid);
        stats.put("completedPayments", completedPayments);
        stats.put("pendingPayments", pendingPayments);
        stats.put("totalTransactions", customerPayments.size());
        stats.put("averagePayment", averagePayment);
        stats.put("lastPaymentDate", getLastPaymentDate(customerPayments));

        return stats;
    }

    @Override
    @Cacheable(value = "paymentStats", key = "'countByStatus'")
    @Transactional(readOnly = true)
    public Map<String, Long> getPaymentCountByStatus() {
        log.info("Generating payment count by status");

        return paymentRecordRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getStatus().name(),
                        Collectors.counting()
                ));
    }

    @Override
    @Cacheable(value = "paymentStats", key = "'revenueByMethod'")
    @Transactional(readOnly = true)
    public Map<String, Double> getRevenueByPaymentMethod() {
        log.info("Generating revenue by payment method");

        return paymentRecordRepository.findAll().stream()
                .filter(payment -> payment.getStatus() == PaymentRecord.PaymentStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        payment -> payment.getPaymentMethod().name(),
                        Collectors.summingDouble(PaymentRecord::getAmount)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean paymentExists(UUID id) {
        return paymentRecordRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentRefundable(UUID id) {
        Optional<PaymentRecord> payment = getPaymentById(id);
        return payment.map(PaymentRecord::isRefundable).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canRetryPayment(UUID id) {
        Optional<PaymentRecord> payment = getPaymentById(id);
        return payment.map(PaymentRecord::canRetry).orElse(false);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public List<PaymentRecord> processBatchPayments(List<PaymentRecord> payments) {
        log.info("Processing batch of {} payments", payments.size());

        return payments.stream()
                .map(this::processPayment)
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Caching(evict = {
            @CacheEvict(value = "payments", allEntries = true),
            @CacheEvict(value = "customerPayments", allEntries = true),
            @CacheEvict(value = "planPayments", allEntries = true),
            @CacheEvict(value = "paymentStats", allEntries = true)
    })
    public void cancelExpiredPendingPayments() {
        log.info("Canceling expired pending payments");

        LocalDateTime expirationThreshold = LocalDateTime.now().minusHours(24);

        List<PaymentRecord> expiredPayments = paymentRecordRepository.findAll().stream()
                .filter(payment -> payment.getStatus() == PaymentRecord.PaymentStatus.PENDING)
                .filter(payment -> payment.getPaymentDate().isBefore(expirationThreshold))
                .collect(Collectors.toList());

        expiredPayments.forEach(payment -> {
            payment.setStatus(PaymentRecord.PaymentStatus.EXPIRED);
            paymentRecordRepository.save(payment);
            log.info("Canceled expired payment: {}", payment.getPaymentReference());
        });

        log.info("Canceled {} expired pending payments", expiredPayments.size());
    }

    // Private helper methods

    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    private void validatePaymentStatusTransition(PaymentRecord.PaymentStatus current, PaymentRecord.PaymentStatus next) {
        // Define valid payment status transitions
        switch (current) {
            case PENDING:
                if (next != PaymentRecord.PaymentStatus.COMPLETED &&
                        next != PaymentRecord.PaymentStatus.FAILED &&
                        next != PaymentRecord.PaymentStatus.CANCELLED &&
                        next != PaymentRecord.PaymentStatus.EXPIRED) {
                    throw new IllegalStateException("Invalid status transition from PENDING to " + next);
                }
                break;
            case COMPLETED:
                if (next != PaymentRecord.PaymentStatus.REFUNDED &&
                        next != PaymentRecord.PaymentStatus.CHARGEBACK) {
                    throw new IllegalStateException("Completed payments can only be refunded or charged back");
                }
                break;
            case FAILED:
                if (next != PaymentRecord.PaymentStatus.PENDING) {
                    throw new IllegalStateException("Failed payments can only be retried (set to PENDING)");
                }
                break;
            case REFUNDED:
            case CANCELLED:
            case CHARGEBACK:
            case EXPIRED:
                throw new IllegalStateException("Cannot change status from terminal state: " + current);
        }
    }

    private void updateProtectionPlanAfterPayment(ProtectionPlan protectionPlan) {
        // Logic to update protection plan after successful payment
        if (protectionPlan.getStatus() == ProtectionPlan.PlanStatus.INACTIVE ||
                protectionPlan.getStatus() == ProtectionPlan.PlanStatus.EXPIRED) {
            protectionPlan.setStatus(ProtectionPlan.PlanStatus.ACTIVE);

            // Extend end date by plan duration (assuming monthly plans)
            protectionPlan.setEndDate(protectionPlan.getEndDate().plusMonths(1));

            log.info("Protection plan activated and extended after payment: {}", protectionPlan.getId());
        }
    }

    private void handleProtectionPlanAfterRefund(ProtectionPlan protectionPlan) {
        // Logic to handle protection plan after refund
        if (protectionPlan.getStatus() == ProtectionPlan.PlanStatus.ACTIVE) {
            protectionPlan.setStatus(ProtectionPlan.PlanStatus.SUSPENDED);
            log.info("Protection plan suspended after refund: {}", protectionPlan.getId());
        }
    }

    private LocalDateTime getLastPaymentDate(List<PaymentRecord> payments) {
        return payments.stream()
                .filter(p -> p.getStatus() == PaymentRecord.PaymentStatus.COMPLETED)
                .map(PaymentRecord::getPaymentDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}