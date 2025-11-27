package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.ProtectionPlan;
import com.assurant.cph.core.repository.ProtectionPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
public class ProtectionPlanServiceImpl implements ProtectionPlanService {

    private final ProtectionPlanRepository protectionPlanRepository;
    private final CustomerService customerService;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "protectionPlans", allEntries = true),
            @CacheEvict(value = "customerPlans", key = "#protectionPlan.customer.id")
    })
    public ProtectionPlan createProtectionPlan(ProtectionPlan protectionPlan) {
        log.info("Creating protection plan for customer: {}", protectionPlan.getCustomer().getId());

        // Validate customer exists
        if (!customerService.customerExists(protectionPlan.getCustomer().getId())) {
            throw new IllegalArgumentException("Customer not found with ID: " + protectionPlan.getCustomer().getId());
        }

        // Validate dates
        if (protectionPlan.getStartDate().isAfter(protectionPlan.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (protectionPlan.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        return protectionPlanRepository.save(protectionPlan);
    }

    @Override
    @Cacheable(value = "protectionPlan", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ProtectionPlan> getProtectionPlanById(UUID id) {
        log.info("Fetching protection plan by ID: {}", id);
        return protectionPlanRepository.findById(id);
    }

    @Override
    @Cacheable(value = "protectionPlans")
    @Transactional(readOnly = true)
    public List<ProtectionPlan> getAllProtectionPlans() {
        log.info("Fetching all protection plans");
        return protectionPlanRepository.findAll();
    }

    @Override
    @Cacheable(value = "customerPlans", key = "#customerId")
    @Transactional(readOnly = true)
    public List<ProtectionPlan> getProtectionPlansByCustomerId(UUID customerId) {
        log.info("Fetching protection plans for customer: {}", customerId);
        return protectionPlanRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectionPlan> getActiveProtectionPlansByCustomerId(UUID customerId) {
        log.info("Fetching active protection plans for customer: {}", customerId);
        return protectionPlanRepository.findActivePlansByCustomerId(customerId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "protectionPlans", allEntries = true),
            @CacheEvict(value = "protectionPlan", key = "#id"),
            @CacheEvict(value = "customerPlans", allEntries = true)
    })
    public ProtectionPlan updateProtectionPlan(UUID id, ProtectionPlan planDetails) {
        log.info("Updating protection plan with ID: {}", id);

        ProtectionPlan existingPlan = protectionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Protection plan not found with ID: " + id));

        // Only allow updates to certain fields
        existingPlan.setName(planDetails.getName());
        existingPlan.setDescription(planDetails.getDescription());
        existingPlan.setPremiumAmount(planDetails.getPremiumAmount());
        existingPlan.setCoverageLimit(planDetails.getCoverageLimit());
        existingPlan.setDeductible(planDetails.getDeductible());

        return protectionPlanRepository.save(existingPlan);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "protectionPlans", allEntries = true),
            @CacheEvict(value = "protectionPlan", key = "#id"),
            @CacheEvict(value = "customerPlans", allEntries = true)
    })
    public void cancelProtectionPlan(UUID id) {
        log.info("Cancelling protection plan with ID: {}", id);

        ProtectionPlan plan = protectionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Protection plan not found with ID: " + id));

        if (plan.getStatus() != ProtectionPlan.PlanStatus.ACTIVE) {
            throw new IllegalStateException("Only active protection plans can be cancelled");
        }

        plan.setStatus(ProtectionPlan.PlanStatus.CANCELLED);
        protectionPlanRepository.save(plan);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "protectionPlans", allEntries = true),
            @CacheEvict(value = "protectionPlan", key = "#id"),
            @CacheEvict(value = "customerPlans", allEntries = true)
    })
    public void renewProtectionPlan(UUID id, int months) {
        log.info("Renewing protection plan with ID: {} for {} months", id, months);

        ProtectionPlan plan = protectionPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Protection plan not found with ID: " + id));

        if (plan.getStatus() != ProtectionPlan.PlanStatus.ACTIVE &&
                plan.getStatus() != ProtectionPlan.PlanStatus.EXPIRED) {
            throw new IllegalStateException("Only active or expired protection plans can be renewed");
        }

        LocalDateTime newEndDate = plan.getEndDate().plusMonths(months);
        plan.setEndDate(newEndDate);
        plan.setStatus(ProtectionPlan.PlanStatus.ACTIVE);

        protectionPlanRepository.save(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtectionPlan> getExpiredPlans() {
        log.info("Fetching expired protection plans");
        return protectionPlanRepository.findExpiredPlans(LocalDateTime.now());
    }
}