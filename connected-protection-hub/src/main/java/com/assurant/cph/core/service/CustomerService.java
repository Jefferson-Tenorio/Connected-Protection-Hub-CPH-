package com.assurant.cph.core.service;

import com.assurant.cph.core.domain.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    Customer createCustomer(Customer customer);
    Optional<Customer> getCustomerById(UUID id);
    List<Customer> getAllCustomers();
    Customer updateCustomer(UUID id, Customer customerDetails);
    // void deleteCustomer(UUID id);
    Optional<Customer> getCustomerByEmail(String email);
    List<Customer> searchCustomersByName(String name);
    List<Customer> getCustomersByCity(String city);
    boolean customerExists(UUID id);
}