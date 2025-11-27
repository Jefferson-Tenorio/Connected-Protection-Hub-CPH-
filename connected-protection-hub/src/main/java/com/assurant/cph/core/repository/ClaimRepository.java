package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByProtectionPlanId(UUID protectionPlanId);
    List<Claim> findByProtectionPlanCustomerId(UUID customerId);

    @Query("SELECT c FROM Claim c WHERE c.status = :status")
    List<Claim> findByStatus(@Param("status") Claim.ClaimStatus status);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.protectionPlan.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") UUID customerId);
}