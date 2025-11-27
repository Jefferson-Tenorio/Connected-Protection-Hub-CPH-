package com.assurant.cph.core.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "vehicles")
@PrimaryKeyJoinColumn(name = "asset_id")
@Getter
@Setter
@SuperBuilder // Mude para @SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends ProtectedAsset {

    @NotBlank(message = "License plate is required")
    @Column(unique = true, nullable = false)
    private String licensePlate;

    @NotBlank(message = "Make is required")
    @Column(nullable = false)
    private String make;

    @NotBlank(message = "Model is required")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Manufacturing year is required")
    private Integer manufacturingYear;

    private String color;
    private String chassisNumber;
    private String fuelType;
    private Integer engineCapacity;
    private Integer mileage;
}