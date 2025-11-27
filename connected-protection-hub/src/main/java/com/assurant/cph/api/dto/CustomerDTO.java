package com.assurant.cph.api.dto;

import com.assurant.cph.core.domain.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer data transfer object")
public class CustomerDTO {

    @Schema(description = "Unique identifier of the customer", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @NotBlank(message = "Full name is required")
    @Schema(description = "Full name of the customer", example = "John Doe", required = true)
    private String fullName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email address of the customer", example = "john.doe@example.com", required = true)
    private String email;

    @Pattern(regexp = "\\+?[0-9\\s\\-\\(\\)]{10,}", message = "Phone number should be valid")
    @Schema(description = "Phone number of the customer", example = "+1-555-0123")
    private String phoneNumber;

    @NotBlank(message = "Document number is required")
    @Schema(description = "Document number (CPF, CNPJ, etc.)", example = "123.456.789-00", required = true)
    private String documentNumber;

    @NotNull(message = "Document type is required")
    @Schema(description = "Type of document", required = true)
    private Customer.DocumentType documentType;

    @Valid
    @Schema(description = "Address information")
    private AddressDTO address;

    @Schema(description = "Date when the customer was created")
    private LocalDateTime createdAt;

    @Schema(description = "Date when the customer was last updated")
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Address information")
    public static class AddressDTO {

        @Schema(description = "Street address", example = "123 Main St")
        private String street;

        @Schema(description = "City", example = "New York")
        private String city;

        @Schema(description = "State or province", example = "NY")
        private String state;

        @Schema(description = "Postal code", example = "10001")
        private String postalCode;

        @Schema(description = "Country", example = "USA")
        private String country;
    }
}