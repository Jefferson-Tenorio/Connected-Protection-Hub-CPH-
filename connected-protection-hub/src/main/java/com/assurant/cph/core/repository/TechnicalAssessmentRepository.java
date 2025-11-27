package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.TechnicalAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TechnicalAssessmentRepository extends JpaRepository<TechnicalAssessment, UUID> {

    List<TechnicalAssessment> findByAssessorName(String assessorName);
    List<TechnicalAssessment> findByResult(TechnicalAssessment.AssessmentResult result);

    @Query("SELECT ta FROM TechnicalAssessment ta WHERE ta.claim.id = :claimId")
    List<TechnicalAssessment> findByClaimId(@Param("claimId") UUID claimId);

    @Query("SELECT ta FROM TechnicalAssessment ta WHERE ta.coveredByInsurance = true")
    List<TechnicalAssessment> findInsuranceCoveredAssessments();
}