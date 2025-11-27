package com.assurant.cph.core.mapper;

import com.assurant.cph.api.dto.PaymentRecordDTO;
import com.assurant.cph.core.domain.PaymentRecord;
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
public class PaymentRecordMapperImpl implements PaymentRecordMapper {

    @Override
    public PaymentRecord toEntity(PaymentRecordDTO paymentRecordDTO) {
        if ( paymentRecordDTO == null ) {
            return null;
        }

        PaymentRecord paymentRecord = new PaymentRecord();

        paymentRecord.setProtectionPlan( mapProtectionPlanIdToEntity( paymentRecordDTO.getProtectionPlanId() ) );
        paymentRecord.setId( paymentRecordDTO.getId() );
        paymentRecord.setPaymentReference( paymentRecordDTO.getPaymentReference() );
        paymentRecord.setAmount( paymentRecordDTO.getAmount() );
        paymentRecord.setPaymentDate( paymentRecordDTO.getPaymentDate() );
        paymentRecord.setPaymentMethod( paymentRecordDTO.getPaymentMethod() );
        paymentRecord.setStatus( paymentRecordDTO.getStatus() );
        paymentRecord.setTransactionId( paymentRecordDTO.getTransactionId() );
        paymentRecord.setPayerInfo( paymentRecordDTO.getPayerInfo() );
        paymentRecord.setPaymentDetails( paymentRecordDTO.getPaymentDetails() );
        paymentRecord.setCreatedAt( paymentRecordDTO.getCreatedAt() );
        paymentRecord.setUpdatedAt( paymentRecordDTO.getUpdatedAt() );

        return paymentRecord;
    }

    @Override
    public PaymentRecordDTO toDTO(PaymentRecord paymentRecord) {
        if ( paymentRecord == null ) {
            return null;
        }

        PaymentRecordDTO.PaymentRecordDTOBuilder paymentRecordDTO = PaymentRecordDTO.builder();

        paymentRecordDTO.protectionPlanId( paymentRecordProtectionPlanId( paymentRecord ) );
        paymentRecordDTO.id( paymentRecord.getId() );
        paymentRecordDTO.paymentReference( paymentRecord.getPaymentReference() );
        paymentRecordDTO.amount( paymentRecord.getAmount() );
        paymentRecordDTO.paymentDate( paymentRecord.getPaymentDate() );
        paymentRecordDTO.paymentMethod( paymentRecord.getPaymentMethod() );
        paymentRecordDTO.status( paymentRecord.getStatus() );
        paymentRecordDTO.transactionId( paymentRecord.getTransactionId() );
        paymentRecordDTO.payerInfo( paymentRecord.getPayerInfo() );
        paymentRecordDTO.paymentDetails( paymentRecord.getPaymentDetails() );
        paymentRecordDTO.createdAt( paymentRecord.getCreatedAt() );
        paymentRecordDTO.updatedAt( paymentRecord.getUpdatedAt() );

        return paymentRecordDTO.build();
    }

    @Override
    public PaymentRecord updateEntityFromDTO(PaymentRecordDTO paymentRecordDTO, PaymentRecord paymentRecord) {
        if ( paymentRecordDTO == null ) {
            return paymentRecord;
        }

        paymentRecord.setProtectionPlan( mapProtectionPlanIdToEntity( paymentRecordDTO.getProtectionPlanId() ) );
        paymentRecord.setPaymentReference( paymentRecordDTO.getPaymentReference() );
        paymentRecord.setAmount( paymentRecordDTO.getAmount() );
        paymentRecord.setPaymentDate( paymentRecordDTO.getPaymentDate() );
        paymentRecord.setPaymentMethod( paymentRecordDTO.getPaymentMethod() );
        paymentRecord.setStatus( paymentRecordDTO.getStatus() );
        paymentRecord.setTransactionId( paymentRecordDTO.getTransactionId() );
        paymentRecord.setPayerInfo( paymentRecordDTO.getPayerInfo() );
        paymentRecord.setPaymentDetails( paymentRecordDTO.getPaymentDetails() );

        return paymentRecord;
    }

    private UUID paymentRecordProtectionPlanId(PaymentRecord paymentRecord) {
        if ( paymentRecord == null ) {
            return null;
        }
        ProtectionPlan protectionPlan = paymentRecord.getProtectionPlan();
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
