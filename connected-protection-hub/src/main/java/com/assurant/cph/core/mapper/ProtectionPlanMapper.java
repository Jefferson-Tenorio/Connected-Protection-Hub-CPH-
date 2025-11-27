package com.assurant.cph.core.mapper;

import com.assurant.cph.api.dto.ProtectionPlanDTO;
import com.assurant.cph.core.domain.ProtectionPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public interface ProtectionPlanMapper {

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "protectedAsset", ignore = true)
    @Mapping(target = "claims", ignore = true)
    @Mapping(target = "paymentRecords", ignore = true)
    ProtectionPlan toEntity(ProtectionPlanDTO protectionPlanDTO);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "protectedAsset.id", target = "assetId")
    ProtectionPlanDTO toDTO(ProtectionPlan protectionPlan);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "protectedAsset", ignore = true)
    @Mapping(target = "claims", ignore = true)
    @Mapping(target = "paymentRecords", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProtectionPlan updateEntityFromDTO(ProtectionPlanDTO protectionPlanDTO, @org.mapstruct.MappingTarget ProtectionPlan protectionPlan);
}