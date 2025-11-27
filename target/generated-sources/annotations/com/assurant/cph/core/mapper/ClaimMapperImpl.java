package com.assurant.cph.core.mapper;

import com.assurant.cph.api.dto.ClaimDTO;
import com.assurant.cph.core.domain.Claim;
import com.assurant.cph.core.domain.ProtectionPlan;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-26T13:39:57-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class ClaimMapperImpl implements ClaimMapper {

    @Override
    public Claim toEntity(ClaimDTO claimDTO) {
        if ( claimDTO == null ) {
            return null;
        }

        Claim.ClaimBuilder claim = Claim.builder();

        claim.id( claimDTO.getId() );
        claim.claimNumber( claimDTO.getClaimNumber() );
        claim.incidentDate( claimDTO.getIncidentDate() );
        claim.description( claimDTO.getDescription() );
        claim.status( claimDTO.getStatus() );
        claim.claimType( claimDTO.getClaimType() );
        claim.claimedAmount( claimDTO.getClaimedAmount() );
        claim.approvedAmount( claimDTO.getApprovedAmount() );
        claim.createdAt( claimDTO.getCreatedAt() );
        claim.updatedAt( claimDTO.getUpdatedAt() );

        return claim.build();
    }

    @Override
    public ClaimDTO toDTO(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        ClaimDTO.ClaimDTOBuilder claimDTO = ClaimDTO.builder();

        claimDTO.protectionPlanId( claimProtectionPlanId( claim ) );
        claimDTO.id( claim.getId() );
        claimDTO.claimNumber( claim.getClaimNumber() );
        claimDTO.incidentDate( claim.getIncidentDate() );
        claimDTO.description( claim.getDescription() );
        claimDTO.status( claim.getStatus() );
        claimDTO.claimType( claim.getClaimType() );
        claimDTO.claimedAmount( claim.getClaimedAmount() );
        claimDTO.approvedAmount( claim.getApprovedAmount() );
        claimDTO.createdAt( claim.getCreatedAt() );
        claimDTO.updatedAt( claim.getUpdatedAt() );

        return claimDTO.build();
    }

    @Override
    public Claim updateEntityFromDTO(ClaimDTO claimDTO, Claim claim) {
        if ( claimDTO == null ) {
            return claim;
        }

        claim.setIncidentDate( claimDTO.getIncidentDate() );
        claim.setDescription( claimDTO.getDescription() );
        claim.setStatus( claimDTO.getStatus() );
        claim.setClaimType( claimDTO.getClaimType() );
        claim.setClaimedAmount( claimDTO.getClaimedAmount() );
        claim.setApprovedAmount( claimDTO.getApprovedAmount() );

        return claim;
    }

    private UUID claimProtectionPlanId(Claim claim) {
        if ( claim == null ) {
            return null;
        }
        ProtectionPlan protectionPlan = claim.getProtectionPlan();
        if ( protectionPlan == null ) {
            return null;
        }
        UUID id = protectionPlan.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
