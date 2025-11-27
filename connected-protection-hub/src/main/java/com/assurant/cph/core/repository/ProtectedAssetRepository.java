package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.ProtectedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProtectedAssetRepository extends JpaRepository<ProtectedAsset, UUID> {

    List<ProtectedAsset> findByCustomerId(UUID customerId);
    Optional<ProtectedAsset> findBySerialNumber(String serialNumber);

    @Query("SELECT pa FROM ProtectedAsset pa WHERE pa.customer.id = :customerId AND pa.status = 'ACTIVE'")
    List<ProtectedAsset> findActiveAssetsByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT COUNT(pa) FROM ProtectedAsset pa WHERE pa.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") UUID customerId);
}