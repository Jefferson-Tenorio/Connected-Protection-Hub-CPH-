package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.RepairOrder;
import com.assurant.cph.core.repository.RepairOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final ClaimService claimService;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "repairOrders", allEntries = true),
            @CacheEvict(value = "claimRepairOrders", allEntries = true),
            @CacheEvict(value = "customerRepairOrders", allEntries = true)
    })
    public RepairOrder createRepairOrder(RepairOrder repairOrder) {
        log.info("Creating repair order for claim: {}", repairOrder.getClaim().getId());

        // Validate claim exists
        var claim = claimService.getClaimById(repairOrder.getClaim().getId())
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with ID: " + repairOrder.getClaim().getId()));

        // Check if repair order already exists for this claim
        if (repairOrderRepository.findByClaimId(repairOrder.getClaim().getId()).isPresent()) {
            throw new IllegalStateException("Repair order already exists for claim: " + repairOrder.getClaim().getId());
        }

        // Validate estimated completion is in the future
        if (repairOrder.getEstimatedCompletion() != null &&
                repairOrder.getEstimatedCompletion().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Estimated completion date must be in the future");
        }

        RepairOrder savedOrder = repairOrderRepository.save(repairOrder);
        log.info("Repair order created successfully: {}", savedOrder.getRepairOrderNumber());

        return savedOrder;
    }

    @Override
    @Cacheable(value = "repairOrders")
    @Transactional(readOnly = true)
    public List<RepairOrder> getAllRepairOrders() {
        log.info("Fetching all repair orders");
        return repairOrderRepository.findAll();
    }

    @Override
    @Cacheable(value = "repairOrder", key = "#id")
    @Transactional(readOnly = true)
    public Optional<RepairOrder> getRepairOrderById(UUID id) {
        log.info("Fetching repair order by ID: {}", id);
        return repairOrderRepository.findById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "repairOrders", allEntries = true),
            @CacheEvict(value = "repairOrder", key = "#id"),
            @CacheEvict(value = "claimRepairOrders", allEntries = true),
            @CacheEvict(value = "customerRepairOrders", allEntries = true)
    })
    public RepairOrder updateRepairOrder(UUID id, RepairOrder repairOrderDetails) {
        log.info("Updating repair order with ID: {}", id);

        RepairOrder existingOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair order not found with ID: " + id));

        // Only allow updates to certain fields
        if (repairOrderDetails.getRepairProvider() != null) {
            existingOrder.setRepairProvider(repairOrderDetails.getRepairProvider());
        }

        if (repairOrderDetails.getProviderContact() != null) {
            existingOrder.setProviderContact(repairOrderDetails.getProviderContact());
        }

        if (repairOrderDetails.getProviderAddress() != null) {
            existingOrder.setProviderAddress(repairOrderDetails.getProviderAddress());
        }

        if (repairOrderDetails.getRepairDescription() != null) {
            existingOrder.setRepairDescription(repairOrderDetails.getRepairDescription());
        }

        if (repairOrderDetails.getDiagnosedIssue() != null) {
            existingOrder.setDiagnosedIssue(repairOrderDetails.getDiagnosedIssue());
        }

        if (repairOrderDetails.getEstimatedCompletion() != null) {
            if (repairOrderDetails.getEstimatedCompletion().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Estimated completion date must be in the future");
            }
            existingOrder.setEstimatedCompletion(repairOrderDetails.getEstimatedCompletion());
        }

        if (repairOrderDetails.getEstimatedCost() != null) {
            existingOrder.setEstimatedCost(repairOrderDetails.getEstimatedCost());
        }

        if (repairOrderDetails.getTechnicianNotes() != null) {
            existingOrder.setTechnicianNotes(repairOrderDetails.getTechnicianNotes());
        }

        return repairOrderRepository.save(existingOrder);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "repairOrders", allEntries = true),
            @CacheEvict(value = "repairOrder", key = "#id"),
            @CacheEvict(value = "claimRepairOrders", allEntries = true),
            @CacheEvict(value = "customerRepairOrders", allEntries = true)
    })
    public void deleteRepairOrder(UUID id) {
        log.info("Deleting repair order with ID: {}", id);

        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair order not found with ID: " + id));

        // Only allow deletion of pending or cancelled repair orders
        if (repairOrder.getStatus() == RepairOrder.RepairStatus.COMPLETED ||
                repairOrder.getStatus() == RepairOrder.RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot delete completed or in-progress repair orders");
        }

        repairOrderRepository.delete(repairOrder);
        log.info("Repair order deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RepairOrder> getRepairOrderByNumber(String repairOrderNumber) {
        log.info("Fetching repair order by number: {}", repairOrderNumber);
        return repairOrderRepository.findByRepairOrderNumber(repairOrderNumber);
    }

    @Override
    @Cacheable(value = "claimRepairOrder", key = "#claimId")
    @Transactional(readOnly = true)
    public Optional<RepairOrder> getRepairOrderByClaimId(UUID claimId) {
        log.info("Fetching repair order for claim: {}", claimId);
        return repairOrderRepository.findByClaimId(claimId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getRepairOrdersByStatus(RepairOrder.RepairStatus status) {
        log.info("Fetching repair orders with status: {}", status);
        return repairOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getRepairOrdersByProvider(String provider) {
        log.info("Fetching repair orders for provider: {}", provider);
        return repairOrderRepository.findByRepairProviderContainingIgnoreCase(provider);
    }

    @Override
    @Cacheable(value = "customerRepairOrders", key = "#customerId")
    @Transactional(readOnly = true)
    public List<RepairOrder> getRepairOrdersByCustomerId(UUID customerId) {
        log.info("Fetching repair orders for customer: {}", customerId);
        return repairOrderRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getOverdueRepairOrders() {
        log.info("Fetching overdue repair orders");
        return repairOrderRepository.findOverdueRepairOrders(LocalDateTime.now());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "repairOrders", allEntries = true),
            @CacheEvict(value = "repairOrder", key = "#id"),
            @CacheEvict(value = "claimRepairOrders", allEntries = true),
            @CacheEvict(value = "customerRepairOrders", allEntries = true)
    })
    public RepairOrder updateRepairOrderStatus(UUID id, RepairOrder.RepairStatus status) {
        log.info("Updating repair order status for ID: {} to {}", id, status);

        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair order not found with ID: " + id));

        // Validate status transition
        validateStatusTransition(repairOrder.getStatus(), status);

        repairOrder.setStatus(status);

        // If moving to completed, set actual completion date
        if (status == RepairOrder.RepairStatus.COMPLETED && repairOrder.getActualCompletion() == null) {
            repairOrder.setActualCompletion(LocalDateTime.now());
        }

        RepairOrder updatedOrder = repairOrderRepository.save(repairOrder);
        log.info("Repair order status updated successfully: {} -> {}", id, status);

        return updatedOrder;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "repairOrders", allEntries = true),
            @CacheEvict(value = "repairOrder", key = "#id"),
            @CacheEvict(value = "claimRepairOrders", allEntries = true),
            @CacheEvict(value = "customerRepairOrders", allEntries = true)
    })
    public RepairOrder completeRepairOrder(UUID id, Double repairCost, String partsReplaced) {
        log.info("Completing repair order with ID: {}", id);

        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair order not found with ID: " + id));

        if (!repairOrder.canBeCompleted()) {
            throw new IllegalStateException("Repair order cannot be completed in current status: " + repairOrder.getStatus());
        }

        repairOrder.completeRepair(repairCost, partsReplaced);

        RepairOrder completedOrder = repairOrderRepository.save(repairOrder);
        log.info("Repair order completed successfully: {}", id);

        return completedOrder;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "repairOrders", allEntries = true),
            @CacheEvict(value = "repairOrder", key = "#id"),
            @CacheEvict(value = "claimRepairOrders", allEntries = true),
            @CacheEvict(value = "customerRepairOrders", allEntries = true)
    })
    public RepairOrder cancelRepairOrder(UUID id, String reason) {
        log.info("Canceling repair order with ID: {}", id);

        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair order not found with ID: " + id));

        repairOrder.cancelRepair(reason);

        RepairOrder cancelledOrder = repairOrderRepository.save(repairOrder);
        log.info("Repair order cancelled successfully: {}", id);

        return cancelledOrder;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "repairOrders", allEntries = true),
            @CacheEvict(value = "repairOrder", key = "#id"),
            @CacheEvict(value = "claimRepairOrders", allEntries = true),
            @CacheEvict(value = "customerRepairOrders", allEntries = true)
    })
    public RepairOrder assignTechnician(UUID id, String technicianNotes) {
        log.info("Assigning technician to repair order: {}", id);

        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair order not found with ID: " + id));

        repairOrder.setTechnicianNotes(technicianNotes);

        RepairOrder updatedOrder = repairOrderRepository.save(repairOrder);
        log.info("Technician assigned to repair order: {}", id);

        return updatedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getRepairOrderCountByStatus(RepairOrder.RepairStatus status) {
        log.info("Getting repair order count for status: {}", status);
        return repairOrderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRepairCostByProvider(String provider) {
        log.info("Getting average repair cost for provider: {}", provider);
        Double average = repairOrderRepository.findAverageRepairCostByProvider(provider);
        return average != null ? average : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getRepairOrdersNeedingFollowUp() {
        log.info("Getting repair orders needing follow-up");
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        return repairOrderRepository.findRepairOrdersNeedingFollowUp(cutoffDate);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean repairOrderExists(UUID id) {
        return repairOrderRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRepairOrderOverdue(UUID id) {
        Optional<RepairOrder> repairOrder = getRepairOrderById(id);
        return repairOrder.map(RepairOrder::isOverdue).orElse(false);
    }

    // Scheduled task to check for overdue repair orders
    @Scheduled(cron = "0 0 9 * * ?") // Run daily at 9 AM
    public void checkOverdueRepairOrders() {
        log.info("Checking for overdue repair orders");

        List<RepairOrder> overdueOrders = getOverdueRepairOrders();

        if (!overdueOrders.isEmpty()) {
            log.warn("Found {} overdue repair orders", overdueOrders.size());
            // Here you could send notifications, emails, etc.
            overdueOrders.forEach(order ->
                    log.info("Overdue repair order: {} - Provider: {} - Estimated: {}",
                            order.getRepairOrderNumber(), order.getRepairProvider(), order.getEstimatedCompletion())
            );
        }
    }

    // Private helper methods
    private void validateStatusTransition(RepairOrder.RepairStatus current, RepairOrder.RepairStatus next) {
        // Define valid status transitions
        switch (current) {
            case PENDING:
                if (next != RepairOrder.RepairStatus.DIAGNOSIS &&
                        next != RepairOrder.RepairStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from PENDING to " + next);
                }
                break;
            case DIAGNOSIS:
                if (next != RepairOrder.RepairStatus.WAITING_PARTS &&
                        next != RepairOrder.RepairStatus.IN_PROGRESS &&
                        next != RepairOrder.RepairStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from DIAGNOSIS to " + next);
                }
                break;
            case WAITING_PARTS:
                if (next != RepairOrder.RepairStatus.IN_PROGRESS &&
                        next != RepairOrder.RepairStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from WAITING_PARTS to " + next);
                }
                break;
            case IN_PROGRESS:
                if (next != RepairOrder.RepairStatus.COMPLETED &&
                        next != RepairOrder.RepairStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from IN_PROGRESS to " + next);
                }
                break;
            case COMPLETED:
            case CANCELLED:
                throw new IllegalStateException("Cannot change status from terminal state: " + current);
        }
    }
}