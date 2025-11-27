package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.Claim;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClaimService {

    Claim createClaim(Claim claim);
    Optional<Claim> getClaimById(UUID id);
    Optional<Claim> getClaimByClaimNumber(String claimNumber);
    List<Claim> getAllClaims();
    List<Claim> getClaimsByCustomerId(UUID customerId);
    List<Claim> getClaimsByProtectionPlanId(UUID protectionPlanId);
    Claim updateClaimStatus(UUID id, Claim.ClaimStatus status);
    Claim assignAssessment(UUID claimId, UUID assessmentId);
    List<Claim> getClaimsByStatus(Claim.ClaimStatus status);
}