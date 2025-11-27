package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.RepairOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepairOrderService {

    // Basic CRUD operations
    RepairOrder createRepairOrder(RepairOrder repairOrder);
    List<RepairOrder> getAllRepairOrders();
    Optional<RepairOrder> getRepairOrderById(UUID id);
    RepairOrder updateRepairOrder(UUID id, RepairOrder repairOrderDetails);
    void deleteRepairOrder(UUID id);

    // Search operations
    Optional<RepairOrder> getRepairOrderByNumber(String repairOrderNumber);
    Optional<RepairOrder> getRepairOrderByClaimId(UUID claimId);
    List<RepairOrder> getRepairOrdersByStatus(RepairOrder.RepairStatus status);
    List<RepairOrder> getRepairOrdersByProvider(String provider);
    List<RepairOrder> getRepairOrdersByCustomerId(UUID customerId);
    List<RepairOrder> getOverdueRepairOrders();

    // Business operations
    RepairOrder updateRepairOrderStatus(UUID id, RepairOrder.RepairStatus status);
    RepairOrder completeRepairOrder(UUID id, Double repairCost, String partsReplaced);
    RepairOrder cancelRepairOrder(UUID id, String reason);
    RepairOrder assignTechnician(UUID id, String technicianNotes);

    // Analytics
    Long getRepairOrderCountByStatus(RepairOrder.RepairStatus status);
    Double getAverageRepairCostByProvider(String provider);
    List<RepairOrder> getRepairOrdersNeedingFollowUp();

    // Validation
    boolean repairOrderExists(UUID id);
    boolean isRepairOrderOverdue(UUID id);
}