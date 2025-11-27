package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.ElectronicDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ElectronicDeviceRepository extends JpaRepository<ElectronicDevice, UUID> {

    Optional<ElectronicDevice> findByImei(String imei);
    List<ElectronicDevice> findByBrand(String brand);
    List<ElectronicDevice> findByDeviceType(String deviceType);

    @Query("SELECT ed FROM ElectronicDevice ed WHERE ed.customer.id = :customerId")
    List<ElectronicDevice> findByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT COUNT(ed) FROM ElectronicDevice ed WHERE ed.brand = :brand")
    Long countByBrand(@Param("brand") String brand);
}