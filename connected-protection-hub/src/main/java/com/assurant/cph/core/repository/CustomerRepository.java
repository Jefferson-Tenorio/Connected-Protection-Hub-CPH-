package com.assurant.cph.core.repository;

import com.assurant.cph.core.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByDocumentNumber(String documentNumber);
    List<Customer> findByFullNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Customer c WHERE c.address.city = :city")
    List<Customer> findByCity(@Param("city") String city);

    boolean existsByEmail(String email);
    boolean existsByDocumentNumber(String documentNumber);
}

