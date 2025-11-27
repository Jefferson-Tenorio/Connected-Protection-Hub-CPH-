package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.ProtectionPlan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProtectionPlanService {

    ProtectionPlan createProtectionPlan(ProtectionPlan protectionPlan);
    Optional<ProtectionPlan> getProtectionPlanById(UUID id);
    List<ProtectionPlan> getAllProtectionPlans();
    List<ProtectionPlan> getProtectionPlansByCustomerId(UUID customerId);
    List<ProtectionPlan> getActiveProtectionPlansByCustomerId(UUID customerId);
    ProtectionPlan updateProtectionPlan(UUID id, ProtectionPlan planDetails);
    void cancelProtectionPlan(UUID id);
    void renewProtectionPlan(UUID id, int months);
    List<ProtectionPlan> getExpiredPlans();
}