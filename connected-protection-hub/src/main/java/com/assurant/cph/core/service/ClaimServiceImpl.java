package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.Claim;
import com.assurant.cph.core.domain.TechnicalAssessment;
import com.assurant.cph.core.repository.ClaimRepository;
import com.assurant.cph.core.repository.TechnicalAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final TechnicalAssessmentRepository technicalAssessmentRepository;
    private final ProtectionPlanService protectionPlanService;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "claims", allEntries = true),
            @CacheEvict(value = "customerClaims", allEntries = true)
    })
    public Claim createClaim(Claim claim) {
        log.info("Creating new claim for protection plan: {}", claim.getProtectionPlan().getId());

        // Validate protection plan exists and is active
        var protectionPlan = protectionPlanService.getProtectionPlanById(claim.getProtectionPlan().getId())
                .orElseThrow(() -> new IllegalArgumentException("Protection plan not found"));

        if (protectionPlan.getStatus() != com.assurant.cph.core.domain.ProtectionPlan.PlanStatus.ACTIVE) {
            throw new IllegalStateException("Cannot create claim for inactive protection plan");
        }

        // Validate incident date is not in the future
        if (claim.getIncidentDate().isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Incident date cannot be in the future");
        }

        return claimRepository.save(claim);
    }

    @Override
    @Cacheable(value = "claim", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Claim> getClaimById(UUID id) {
        log.info("Fetching claim by ID: {}", id);
        return claimRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Claim> getClaimByClaimNumber(String claimNumber) {
        log.info("Fetching claim by number: {}", claimNumber);
        return claimRepository.findByClaimNumber(claimNumber);
    }

    @Override
    @Cacheable(value = "claims")
    @Transactional(readOnly = true)
    public List<Claim> getAllClaims() {
        log.info("Fetching all claims");
        return claimRepository.findAll();
    }

    @Override
    @Cacheable(value = "customerClaims", key = "#customerId")
    @Transactional(readOnly = true)
    public List<Claim> getClaimsByCustomerId(UUID customerId) {
        log.info("Fetching claims for customer: {}", customerId);
        return claimRepository.findByProtectionPlanCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Claim> getClaimsByProtectionPlanId(UUID protectionPlanId) {
        log.info("Fetching claims for protection plan: {}", protectionPlanId);
        return claimRepository.findByProtectionPlanId(protectionPlanId);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "claims", allEntries = true),
            @CacheEvict(value = "claim", key = "#id"),
            @CacheEvict(value = "customerClaims", allEntries = true)
    })
    public Claim updateClaimStatus(UUID id, Claim.ClaimStatus status) {
        log.info("Updating claim status for ID: {} to {}", id, status);

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with ID: " + id));

        // Validate status transition
        validateStatusTransition(claim.getStatus(), status);

        claim.setStatus(status);
        return claimRepository.save(claim);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "claims", allEntries = true),
            @CacheEvict(value = "claim", key = "#claimId"),
            @CacheEvict(value = "customerClaims", allEntries = true)
    })
    public Claim assignAssessment(UUID claimId, UUID assessmentId) {
        log.info("Assigning assessment {} to claim {}", assessmentId, claimId);

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with ID: " + claimId));

        TechnicalAssessment assessment = technicalAssessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Technical assessment not found with ID: " + assessmentId));

        claim.setTechnicalAssessment(assessment);
        return claimRepository.save(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Claim> getClaimsByStatus(Claim.ClaimStatus status) {
        log.info("Fetching claims with status: {}", status);
        return claimRepository.findByStatus(status);
    }

    private void validateStatusTransition(Claim.ClaimStatus currentStatus, Claim.ClaimStatus newStatus) {
        // Implement status transition validation logic
        // For example, you can't move from COMPLETED back to UNDER_REVIEW
        if (currentStatus == Claim.ClaimStatus.COMPLETED && newStatus != Claim.ClaimStatus.COMPLETED) {
            throw new IllegalStateException("Cannot change status from COMPLETED");
        }

        if (currentStatus == Claim.ClaimStatus.REJECTED && newStatus != Claim.ClaimStatus.REJECTED) {
            throw new IllegalStateException("Cannot change status from REJECTED");
        }
    }
}