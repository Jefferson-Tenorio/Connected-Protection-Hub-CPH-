package com.assurant.cph.core.mapper;

import com.assurant.cph.api.dto.ClaimDTO;
import com.assurant.cph.core.domain.Claim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

    @Mapping(target = "protectionPlan", ignore = true)
    @Mapping(target = "repairOrder", ignore = true)
    @Mapping(target = "technicalAssessment", ignore = true)
    Claim toEntity(ClaimDTO claimDTO);

    @Mapping(source = "protectionPlan.id", target = "protectionPlanId")
    ClaimDTO toDTO(Claim claim);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protectionPlan", ignore = true)
    @Mapping(target = "repairOrder", ignore = true)
    @Mapping(target = "technicalAssessment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "claimNumber", ignore = true)
    Claim updateEntityFromDTO(ClaimDTO claimDTO, @org.mapstruct.MappingTarget Claim claim);
}