package com.assurant.cph.core.mapper;

import com.assurant.cph.api.dto.PaymentRecordDTO;
import com.assurant.cph.core.domain.PaymentRecord;
import com.assurant.cph.core.domain.ProtectionPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PaymentRecordMapper {

    @Mapping(target = "protectionPlan", source = "protectionPlanId", qualifiedByName = "mapProtectionPlanIdToEntity")
    @Mapping(target = "claim", ignore = true)
    PaymentRecord toEntity(PaymentRecordDTO paymentRecordDTO);

    @Mapping(source = "protectionPlan.id", target = "protectionPlanId")
    PaymentRecordDTO toDTO(PaymentRecord paymentRecord);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "protectionPlan", source = "protectionPlanId", qualifiedByName = "mapProtectionPlanIdToEntity")
    @Mapping(target = "claim", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentRecord updateEntityFromDTO(PaymentRecordDTO paymentRecordDTO, @org.mapstruct.MappingTarget PaymentRecord paymentRecord);

    @Named("mapProtectionPlanIdToEntity")
    default ProtectionPlan mapProtectionPlanIdToEntity(UUID protectionPlanId) {
        if (protectionPlanId == null) {
            return null;
        }
        return ProtectionPlan.builder().id(protectionPlanId).build();
    }

    // Custom mapping for specific scenarios
    default PaymentRecord mapToEntityWithDefaults(PaymentRecordDTO dto) {
        PaymentRecord entity = toEntity(dto);

        // Set defaults if not provided in DTO
        if (entity.getStatus() == null) {
            entity.setStatus(PaymentRecord.PaymentStatus.PENDING);
        }

        if (entity.getPaymentDate() == null) {
            entity.setPaymentDate(java.time.LocalDateTime.now());
        }

        return entity;
    }
}