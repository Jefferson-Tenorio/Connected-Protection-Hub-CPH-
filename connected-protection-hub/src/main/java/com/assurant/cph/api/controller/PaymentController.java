package com.assurant.cph.api.controller;

import com.assurant.cph.core.domain.PaymentRecord;
import com.assurant.cph.core.service.PaymentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing and management APIs")
public class PaymentController {

    private final PaymentRecordService paymentRecordService;

    @PostMapping
    @Operation(summary = "Process a new payment", description = "Creates and processes a new payment for a protection plan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Protection plan not found"),
            @ApiResponse(responseCode = "409", description = "Payment reference already exists")
    })
    public ResponseEntity<PaymentRecord> processPayment(
            @Parameter(description = "Payment data")
            @Valid @RequestBody PaymentRecord paymentRecord) {

        log.info("Processing new payment for protection plan: {}", paymentRecord.getProtectionPlan().getId());

        PaymentRecord processedPayment = paymentRecordService.processPayment(paymentRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(processedPayment);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves a list of all payment records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all payments")
    public ResponseEntity<List<PaymentRecord>> getAllPayments() {
        log.info("Fetching all payment records");
        List<PaymentRecord> payments = paymentRecordService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a specific payment by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentRecord> getPaymentById(
            @Parameter(description = "Payment ID")
            @PathVariable UUID id) {

        log.info("Fetching payment by ID: {}", id);

        Optional<PaymentRecord> payment = paymentRecordService.getPaymentById(id);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment", description = "Updates an existing payment's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Transaction ID already exists")
    })
    public ResponseEntity<PaymentRecord> updatePayment(
            @Parameter(description = "Payment ID") @PathVariable UUID id,
            @Parameter(description = "Updated payment data") @Valid @RequestBody PaymentRecord paymentDetails) {

        log.info("Updating payment with ID: {}", id);

        PaymentRecord updatedPayment = paymentRecordService.updatePayment(id, paymentDetails);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Deletes a payment by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete completed or refunded payments")
    })
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "Payment ID")
            @PathVariable UUID id) {

        log.info("Deleting payment with ID: {}", id);
        paymentRecordService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reference/{paymentReference}")
    @Operation(summary = "Get payment by reference", description = "Retrieves a payment by its unique reference number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentRecord> getPaymentByReference(
            @Parameter(description = "Payment reference number")
            @PathVariable String paymentReference) {

        log.info("Fetching payment by reference: {}", paymentReference);

        Optional<PaymentRecord> payment = paymentRecordService.getPaymentByReference(paymentReference);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID", description = "Retrieves a payment by its transaction ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentRecord> getPaymentByTransactionId(
            @Parameter(description = "Transaction ID")
            @PathVariable String transactionId) {

        log.info("Fetching payment by transaction ID: {}", transactionId);

        Optional<PaymentRecord> payment = paymentRecordService.getPaymentByTransactionId(transactionId);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/protection-plan/{protectionPlanId}")
    @Operation(summary = "Get payments by protection plan", description = "Retrieves all payments for a specific protection plan")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved payments")
    public ResponseEntity<List<PaymentRecord>> getPaymentsByProtectionPlanId(
            @Parameter(description = "Protection plan ID")
            @PathVariable UUID protectionPlanId) {

        log.info("Fetching payments for protection plan: {}", protectionPlanId);

        List<PaymentRecord> payments = paymentRecordService.getPaymentsByProtectionPlanId(protectionPlanId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get payments by customer", description = "Retrieves all payments for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved payments")
    public ResponseEntity<List<PaymentRecord>> getPaymentsByCustomerId(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        log.info("Fetching payments for customer: {}", customerId);

        List<PaymentRecord> payments = paymentRecordService.getPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Retrieves all payments with a specific status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved payments")
    public ResponseEntity<List<PaymentRecord>> getPaymentsByStatus(
            @Parameter(description = "Payment status")
            @PathVariable PaymentRecord.PaymentStatus status) {

        log.info("Fetching payments with status: {}", status);

        List<PaymentRecord> payments = paymentRecordService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/method/{paymentMethod}")
    @Operation(summary = "Get payments by method", description = "Retrieves all payments with a specific payment method")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved payments")
    public ResponseEntity<List<PaymentRecord>> getPaymentsByMethod(
            @Parameter(description = "Payment method")
            @PathVariable PaymentRecord.PaymentMethod paymentMethod) {

        log.info("Fetching payments by method: {}", paymentMethod);

        List<PaymentRecord> payments = paymentRecordService.getPaymentsByMethod(paymentMethod);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get payments by date range", description = "Retrieves all payments within a specific date range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved payments")
    public ResponseEntity<List<PaymentRecord>> getPaymentsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd HH:mm:ss)")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "End date (yyyy-MM-dd HH:mm:ss)")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        log.info("Fetching payments between {} and {}", startDate, endDate);

        List<PaymentRecord> payments = paymentRecordService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update payment status", description = "Updates the status of a specific payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
    public ResponseEntity<PaymentRecord> updatePaymentStatus(
            @Parameter(description = "Payment ID") @PathVariable UUID id,
            @Parameter(description = "New status") @RequestParam PaymentRecord.PaymentStatus status) {

        log.info("Updating payment status for ID: {} to {}", id, status);

        PaymentRecord updatedPayment = paymentRecordService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Process refund", description = "Processes a refund for a specific payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "409", description = "Payment cannot be refunded")
    })
    public ResponseEntity<PaymentRecord> processRefund(
            @Parameter(description = "Payment ID")
            @PathVariable UUID id) {

        log.info("Processing refund for payment: {}", id);

        PaymentRecord refundedPayment = paymentRecordService.processRefund(id);
        return ResponseEntity.ok(refundedPayment);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Mark payment as completed", description = "Marks a payment as completed with transaction ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment marked as completed successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentRecord> markAsCompleted(
            @Parameter(description = "Payment ID") @PathVariable UUID id,
            @Parameter(description = "Transaction ID") @RequestParam String transactionId) {

        log.info("Marking payment as completed: {} with transaction ID: {}", id, transactionId);

        PaymentRecord completedPayment = paymentRecordService.markAsCompleted(id, transactionId);
        return ResponseEntity.ok(completedPayment);
    }

    @PostMapping("/{id}/fail")
    @Operation(summary = "Mark payment as failed", description = "Marks a payment as failed with reason")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment marked as failed successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentRecord> markAsFailed(
            @Parameter(description = "Payment ID") @PathVariable UUID id,
            @Parameter(description = "Failure reason") @RequestParam String failureReason) {

        log.info("Marking payment as failed: {} - Reason: {}", id, failureReason);

        PaymentRecord failedPayment = paymentRecordService.markAsFailed(id, failureReason);
        return ResponseEntity.ok(failedPayment);
    }

    @GetMapping("/customer/{customerId}/total")
    @Operation(summary = "Get total paid amount by customer", description = "Calculates the total amount paid by a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully calculated total amount")
    public ResponseEntity<Map<String, Object>> getTotalPaidAmountByCustomer(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        log.info("Calculating total paid amount for customer: {}", customerId);

        Double totalAmount = paymentRecordService.getTotalPaidAmountByCustomer(customerId);

        Map<String, Object> response = Map.of(
                "customerId", customerId,
                "totalPaidAmount", totalAmount
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/statistics")
    @Operation(summary = "Get payment statistics for customer", description = "Retrieves comprehensive payment statistics for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        log.info("Generating payment statistics for customer: {}", customerId);

        Map<String, Object> statistics = paymentRecordService.getPaymentStatistics(customerId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics/count-by-status")
    @Operation(summary = "Get payment count by status", description = "Retrieves count of payments grouped by status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved counts")
    public ResponseEntity<Map<String, Long>> getPaymentCountByStatus() {
        log.info("Generating payment count by status");

        Map<String, Long> counts = paymentRecordService.getPaymentCountByStatus();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/statistics/revenue-by-method")
    @Operation(summary = "Get revenue by payment method", description = "Retrieves total revenue grouped by payment method")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved revenue data")
    public ResponseEntity<Map<String, Double>> getRevenueByPaymentMethod() {
        log.info("Generating revenue by payment method");

        Map<String, Double> revenue = paymentRecordService.getRevenueByPaymentMethod();
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/{id}/refundable")
    @Operation(summary = "Check if payment is refundable", description = "Checks if a specific payment can be refunded")
    @ApiResponse(responseCode = "200", description = "Successfully checked refund status")
    public ResponseEntity<Map<String, Object>> isPaymentRefundable(
            @Parameter(description = "Payment ID")
            @PathVariable UUID id) {

        log.info("Checking if payment is refundable: {}", id);

        boolean refundable = paymentRecordService.isPaymentRefundable(id);

        Map<String, Object> response = Map.of(
                "paymentId", id,
                "refundable", refundable
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/retry")
    @Operation(summary = "Check if payment can be retried", description = "Checks if a specific payment can be retried")
    @ApiResponse(responseCode = "200", description = "Successfully checked retry status")
    public ResponseEntity<Map<String, Object>> canRetryPayment(
            @Parameter(description = "Payment ID")
            @PathVariable UUID id) {

        log.info("Checking if payment can be retried: {}", id);

        boolean canRetry = paymentRecordService.canRetryPayment(id);

        Map<String, Object> response = Map.of(
                "paymentId", id,
                "canRetry", canRetry
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    @Operation(summary = "Process batch payments", description = "Processes multiple payments in a single batch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch payments processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data in batch")
    })
    public ResponseEntity<List<PaymentRecord>> processBatchPayments(
            @Parameter(description = "List of payments to process")
            @Valid @RequestBody List<PaymentRecord> payments) {

        log.info("Processing batch of {} payments", payments.size());

        List<PaymentRecord> processedPayments = paymentRecordService.processBatchPayments(payments);
        return ResponseEntity.ok(processedPayments);
    }
}