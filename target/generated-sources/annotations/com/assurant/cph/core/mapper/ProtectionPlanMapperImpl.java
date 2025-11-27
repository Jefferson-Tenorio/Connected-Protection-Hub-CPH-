package com.assurant.cph.core.mapper;

import com.assurant.cph.api.dto.ProtectionPlanDTO;
import com.assurant.cph.core.domain.Customer;
import com.assurant.cph.core.domain.ProtectedAsset;
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
public class ProtectionPlanMapperImpl implements ProtectionPlanMapper {

    @Override
    public ProtectionPlan toEntity(ProtectionPlanDTO protectionPlanDTO) {
        if ( protectionPlanDTO == null ) {
            return null;
        }

        ProtectionPlan.ProtectionPlanBuilder protectionPlan = ProtectionPlan.builder();

        protectionPlan.id( protectionPlanDTO.getId() );
        protectionPlan.name( protectionPlanDTO.getName() );
        protectionPlan.description( protectionPlanDTO.getDescription() );
        protectionPlan.startDate( protectionPlanDTO.getStartDate() );
        protectionPlan.endDate( protectionPlanDTO.getEndDate() );
        protectionPlan.premiumAmount( protectionPlanDTO.getPremiumAmount() );
        protectionPlan.coverageLimit( protectionPlanDTO.getCoverageLimit() );
        protectionPlan.deductible( protectionPlanDTO.getDeductible() );
        protectionPlan.status( protectionPlanDTO.getStatus() );
        protectionPlan.createdAt( protectionPlanDTO.getCreatedAt() );
        protectionPlan.updatedAt( protectionPlanDTO.getUpdatedAt() );

        return protectionPlan.build();
    }

    @Override
    public ProtectionPlanDTO toDTO(ProtectionPlan protectionPlan) {
        if ( protectionPlan == null ) {
            return null;
        }

        ProtectionPlanDTO.ProtectionPlanDTOBuilder protectionPlanDTO = ProtectionPlanDTO.builder();

        protectionPlanDTO.customerId( protectionPlanCustomerId( protectionPlan ) );
        protectionPlanDTO.assetId( protectionPlanProtectedAssetId( protectionPlan ) );
        protectionPlanDTO.id( protectionPlan.getId() );
        protectionPlanDTO.name( protectionPlan.getName() );
        protectionPlanDTO.description( protectionPlan.getDescription() );
        protectionPlanDTO.startDate( protectionPlan.getStartDate() );
        protectionPlanDTO.endDate( protectionPlan.getEndDate() );
        protectionPlanDTO.premiumAmount( protectionPlan.getPremiumAmount() );
        protectionPlanDTO.coverageLimit( protectionPlan.getCoverageLimit() );
        protectionPlanDTO.deductible( protectionPlan.getDeductible() );
        protectionPlanDTO.status( protectionPlan.getStatus() );
        protectionPlanDTO.createdAt( protectionPlan.getCreatedAt() );
        protectionPlanDTO.updatedAt( protectionPlan.getUpdatedAt() );

        return protectionPlanDTO.build();
    }

    @Override
    public ProtectionPlan updateEntityFromDTO(ProtectionPlanDTO protectionPlanDTO, ProtectionPlan protectionPlan) {
        if ( protectionPlanDTO == null ) {
            return protectionPlan;
        }

        protectionPlan.setName( protectionPlanDTO.getName() );
        protectionPlan.setDescription( protectionPlanDTO.getDescription() );
        protectionPlan.setStartDate( protectionPlanDTO.getStartDate() );
        protectionPlan.setEndDate( protectionPlanDTO.getEndDate() );
        protectionPlan.setPremiumAmount( protectionPlanDTO.getPremiumAmount() );
        protectionPlan.setCoverageLimit( protectionPlanDTO.getCoverageLimit() );
        protectionPlan.setDeductible( protectionPlanDTO.getDeductible() );
        protectionPlan.setStatus( protectionPlanDTO.getStatus() );

        return protectionPlan;
    }

    private UUID protectionPlanCustomerId(ProtectionPlan protectionPlan) {
        if ( protectionPlan == null ) {
            return null;
        }
        Customer customer = protectionPlan.getCustomer();
        if ( customer == null ) {
            return null;
        }
        UUID id = customer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID protectionPlanProtectedAssetId(ProtectionPlan protectionPlan) {
        if ( protectionPlan == null ) {
            return null;
        }
        ProtectedAsset protectedAsset = protectionPlan.getProtectedAsset();
        if ( protectedAsset == null ) {
            return null;
        }
        UUID id = protectedAsset.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
