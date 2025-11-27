package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, UUID> {

    Optional<PaymentRecord> findByPaymentReference(String paymentReference);
    Optional<PaymentRecord> findByTransactionId(String transactionId);
    List<PaymentRecord> findByStatus(PaymentRecord.PaymentStatus status);
    List<PaymentRecord> findByPaymentMethod(PaymentRecord.PaymentMethod paymentMethod);

    @Query("SELECT pr FROM PaymentRecord pr WHERE pr.protectionPlan.id = :protectionPlanId")
    List<PaymentRecord> findByProtectionPlanId(@Param("protectionPlanId") UUID protectionPlanId);

    @Query("SELECT pr FROM PaymentRecord pr WHERE pr.protectionPlan.customer.id = :customerId")
    List<PaymentRecord> findByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT SUM(pr.amount) FROM PaymentRecord pr WHERE pr.protectionPlan.customer.id = :customerId AND pr.status = 'COMPLETED'")
    Double getTotalPaidAmountByCustomer(@Param("customerId") UUID customerId);
}
