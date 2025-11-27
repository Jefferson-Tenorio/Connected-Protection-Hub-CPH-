package com.assurant.cph.api.dto;

import com.assurant.cph.core.domain.PaymentMethod;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestDTO(
        @NotBlank UUID customerId,
        @NotBlank String protectionPlanId,
        @NotNull @Positive BigDecimal amount,
        @NotNull PaymentMethod method
) {}
