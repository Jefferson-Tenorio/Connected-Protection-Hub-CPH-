package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);
    List<Vehicle> findByMake(String make);
    List<Vehicle> findByMakeAndModel(String make, String model);

    @Query("SELECT v FROM Vehicle v WHERE v.customer.id = :customerId")
    List<Vehicle> findByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT v FROM Vehicle v WHERE v.manufacturingYear >= :year")
    List<Vehicle> findByManufacturingYearAfter(@Param("year") Integer year);
}