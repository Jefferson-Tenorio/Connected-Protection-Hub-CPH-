package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.Customer;
import com.assurant.cph.core.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "customers", allEntries = true),
            @CacheEvict(value = "customer", key = "#result.id")
    })
    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer: {}", customer.getEmail());

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + customer.getEmail() + " already exists");
        }

        if (customerRepository.existsByDocumentNumber(customer.getDocumentNumber())) {
            throw new IllegalArgumentException("Customer with document number " + customer.getDocumentNumber() + " already exists");
        }

        return customerRepository.save(customer);
    }

    @Override
    @Cacheable(value = "customer", key = "#id")
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(UUID id) {
        log.info("Fetching customer by ID: {}", id);
        return customerRepository.findById(id);
    }

    @Override
    @Cacheable(value = "customers")
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "customers", allEntries = true),
            @CacheEvict(value = "customer", key = "#id")
    })
    public Customer updateCustomer(UUID id, Customer customerDetails) {
        log.info("Updating customer with ID: {}", id);

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));

        // Check email uniqueness if changed
        if (!existingCustomer.getEmail().equals(customerDetails.getEmail()) &&
                customerRepository.existsByEmail(customerDetails.getEmail())) {
            throw new IllegalArgumentException("Email " + customerDetails.getEmail() + " already exists");
        }

        // Check document number uniqueness if changed
        if (!existingCustomer.getDocumentNumber().equals(customerDetails.getDocumentNumber()) &&
                customerRepository.existsByDocumentNumber(customerDetails.getDocumentNumber())) {
            throw new IllegalArgumentException("Document number " + customerDetails.getDocumentNumber() + " already exists");
        }

        existingCustomer.setFullName(customerDetails.getFullName());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
        existingCustomer.setDocumentNumber(customerDetails.getDocumentNumber());
        existingCustomer.setDocumentType(customerDetails.getDocumentType());
        existingCustomer.setAddress(customerDetails.getAddress());

        return customerRepository.save(existingCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("Fetching customer by email: {}", email);
        return customerRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomersByName(String name) {
        log.info("Searching customers by name: {}", name);
        return customerRepository.findByFullNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getCustomersByCity(String city) {
        log.info("Fetching customers by city: {}", city);
        return customerRepository.findByCity(city);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean customerExists(UUID id) {
        return customerRepository.existsById(id);
    }
}