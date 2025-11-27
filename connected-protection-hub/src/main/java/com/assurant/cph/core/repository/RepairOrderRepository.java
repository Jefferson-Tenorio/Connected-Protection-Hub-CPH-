package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, UUID> {

    // Find by repair order number
    Optional<RepairOrder> findByRepairOrderNumber(String repairOrderNumber);

    // Find by claim ID
    Optional<RepairOrder> findByClaimId(UUID claimId);

    // Find by status
    List<RepairOrder> findByStatus(RepairOrder.RepairStatus status);

    // Find by repair provider (case-insensitive containing)
    List<RepairOrder> findByRepairProviderContainingIgnoreCase(String provider);

    // Find by multiple statuses
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.status IN :statuses")
    List<RepairOrder> findByStatusIn(@Param("statuses") List<RepairOrder.RepairStatus> statuses);

    // Find overdue repair orders
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.estimatedCompletion < :currentDate AND ro.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<RepairOrder> findOverdueRepairOrders(@Param("currentDate") LocalDateTime currentDate);

    // Find repair orders by provider and status
    List<RepairOrder> findByRepairProviderAndStatus(String repairProvider, RepairOrder.RepairStatus status);

    // Find repair orders within date range
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.createdAt BETWEEN :startDate AND :endDate")
    List<RepairOrder> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Count repair orders by status
    @Query("SELECT COUNT(ro) FROM RepairOrder ro WHERE ro.status = :status")
    Long countByStatus(@Param("status") RepairOrder.RepairStatus status);

    // Count completed repair orders by provider
    @Query("SELECT COUNT(ro) FROM RepairOrder ro WHERE ro.repairProvider = :provider AND ro.status = 'COMPLETED'")
    Long countCompletedByProvider(@Param("provider") String provider);

    // Find repair orders with cost greater than specified amount
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.repairCost > :minCost")
    List<RepairOrder> findByRepairCostGreaterThan(@Param("minCost") Double minCost);

    // Find repair orders by claim customer ID
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.claim.protectionPlan.customer.id = :customerId")
    List<RepairOrder> findByCustomerId(@Param("customerId") UUID customerId);

    // Calculate average repair cost by provider
    @Query("SELECT AVG(ro.repairCost) FROM RepairOrder ro WHERE ro.repairProvider = :provider AND ro.status = 'COMPLETED'")
    Double findAverageRepairCostByProvider(@Param("provider") String provider);

    // Find repair orders that need follow-up (completed more than 30 days ago without update)
    @Query("SELECT ro FROM RepairOrder ro WHERE ro.actualCompletion < :cutoffDate AND ro.updatedAt < :cutoffDate")
    List<RepairOrder> findRepairOrdersNeedingFollowUp(@Param("cutoffDate") LocalDateTime cutoffDate);
}