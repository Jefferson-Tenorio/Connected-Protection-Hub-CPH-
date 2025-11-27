package com.assurant.cph.api.dto;

import com.assurant.cph.core.domain.PaymentMethod;
import com.assurant.cph.core.domain.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        String id,
        BigDecimal amount,
        PaymentStatus status,
        PaymentMethod method,
        LocalDateTime processedAt,
        String customerId,
        String protectionPlanId
) {}
