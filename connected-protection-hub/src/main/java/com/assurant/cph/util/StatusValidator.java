package com.assurant.cph.util;

import com.assurant.cph.core.domain.Claim;
import com.assurant.cph.core.domain.ProtectionPlan;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatusValidator {

    public static boolean isValidClaimStatusTransition(Claim.ClaimStatus current, Claim.ClaimStatus next) {
        // Define valid status transitions
        switch (current) {
            case SUBMITTED:
                return next == Claim.ClaimStatus.UNDER_REVIEW || next == Claim.ClaimStatus.REJECTED;
            case UNDER_REVIEW:
                return next == Claim.ClaimStatus.APPROVED || next == Claim.ClaimStatus.REJECTED || next == Claim.ClaimStatus.IN_REPAIR;
            case APPROVED:
                return next == Claim.ClaimStatus.IN_REPAIR || next == Claim.ClaimStatus.COMPLETED;
            case IN_REPAIR:
                return next == Claim.ClaimStatus.COMPLETED || next == Claim.ClaimStatus.CANCELLED;
            case REJECTED:
            case COMPLETED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }

    public static boolean isValidPlanStatusTransition(ProtectionPlan.PlanStatus current, ProtectionPlan.PlanStatus next) {
        // Define valid status transitions for protection plans
        switch (current) {
            case ACTIVE:
                return next == ProtectionPlan.PlanStatus.INACTIVE || next == ProtectionPlan.PlanStatus.CANCELLED || next == ProtectionPlan.PlanStatus.EXPIRED;
            case INACTIVE:
                return next == ProtectionPlan.PlanStatus.ACTIVE || next == ProtectionPlan.PlanStatus.CANCELLED;
            case EXPIRED:
                return next == ProtectionPlan.PlanStatus.ACTIVE; // Renewal
            case CANCELLED:
            case SUSPENDED:
                return false; // Terminal states
            default:
                return false;
        }
    }
}