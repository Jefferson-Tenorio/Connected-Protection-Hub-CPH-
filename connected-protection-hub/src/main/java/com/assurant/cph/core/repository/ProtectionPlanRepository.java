package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.ProtectionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProtectionPlanRepository extends JpaRepository<ProtectionPlan, UUID> {

    List<ProtectionPlan> findByCustomerId(UUID customerId);
    List<ProtectionPlan> findByProtectedAssetId(UUID assetId);

    @Query("SELECT pp FROM ProtectionPlan pp WHERE pp.status = 'ACTIVE' AND pp.endDate < :currentDate")
    List<ProtectionPlan> findExpiredPlans(@Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT pp FROM ProtectionPlan pp WHERE pp.customer.id = :customerId AND pp.status = 'ACTIVE'")
    List<ProtectionPlan> findActivePlansByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT COUNT(pp) FROM ProtectionPlan pp WHERE pp.customer.id = :customerId AND pp.status = 'ACTIVE'")
    Long countActivePlansByCustomerId(@Param("customerId") UUID customerId);
}