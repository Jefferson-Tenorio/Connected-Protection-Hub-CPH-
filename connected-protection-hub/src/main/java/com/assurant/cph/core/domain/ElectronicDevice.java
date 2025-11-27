package com.assurant.cph.core.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "electronic_devices")
@PrimaryKeyJoinColumn(name = "asset_id")
@Getter
@Setter
@SuperBuilder // Mude para @SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ElectronicDevice extends ProtectedAsset {

    @NotBlank(message = "Device type is required")
    @Column(nullable = false)
    private String deviceType; // SMARTPHONE, LAPTOP, TABLET, etc.

    @NotBlank(message = "Brand is required")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Model is required")
    @Column(nullable = false)
    private String model;

    private Integer storageCapacityGB;
    private Integer ramCapacityGB;
    private String processor;
    private String screenSize;
    private String operatingSystem;
    private String imei;
    private String macAddress;
}