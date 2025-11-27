package com.assurant.cph.api.controller;

import com.assurant.cph.api.dto.CustomerDTO;
import com.assurant.cph.core.domain.Customer;
import com.assurant.cph.core.mapper.CustomerMapper;
import com.assurant.cph.core.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Customer with email or document number already exists")
    })
    public ResponseEntity<CustomerDTO> createCustomer(
            @Parameter(description = "Customer data")
            @Valid @RequestBody CustomerDTO customerDTO) {

        log.info("Creating new customer: {}", customerDTO.getEmail());

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer createdCustomer = customerService.createCustomer(customer);
        CustomerDTO createdCustomerDTO = customerMapper.toDTO(createdCustomer);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomerDTO);
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        log.info("Fetching all customers");

        List<CustomerDTO> customers = customerService.getAllCustomers().stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a specific customer by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "Customer ID")
            @PathVariable UUID id) {

        log.info("Fetching customer by ID: {}", id);

        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(c -> ResponseEntity.ok(customerMapper.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Updates an existing customer's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email or document number already exists")
    })
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable UUID id,
            @Parameter(description = "Updated customer data") @Valid @RequestBody CustomerDTO customerDTO) {

        log.info("Updating customer with ID: {}", id);

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        CustomerDTO updatedCustomerDTO = customerMapper.toDTO(updatedCustomer);

        return ResponseEntity.ok(updatedCustomerDTO);
    }

    //@DeleteMapping("/{id}")
    //@Operation(summary = "Delete customer", description = "Deletes a customer by their unique identifier")
    //@ApiResponses({
    //        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
    //        @ApiResponse(responseCode = "404", description = "Customer not found"),
    //        @ApiResponse(responseCode = "409", description = "Customer has active protection plans")
    //})
    //public ResponseEntity<Void> deleteCustomer(
    //        @Parameter(description = "Customer ID")
    //        @PathVariable UUID id) {

    //    log.info("Deleting customer with ID: {}", id);
    //    customerService.deleteCustomer(id);
    //    return ResponseEntity.noContent().build();
    //}

    @GetMapping("/search")
    @Operation(summary = "Search customers by name", description = "Searches customers by full name (case-insensitive)")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    public ResponseEntity<List<CustomerDTO>> searchCustomersByName(
            @Parameter(description = "Name to search for")
            @RequestParam String name) {

        log.info("Searching customers by name: {}", name);

        List<CustomerDTO> customers = customerService.searchCustomersByName(name).stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email", description = "Retrieves a customer by their email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> getCustomerByEmail(
            @Parameter(description = "Email address")
            @PathVariable String email) {

        log.info("Fetching customer by email: {}", email);

        Optional<Customer> customer = customerService.getCustomerByEmail(email);
        return customer.map(c -> ResponseEntity.ok(customerMapper.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get customers by city", description = "Retrieves all customers in a specific city")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customers")
    public ResponseEntity<List<CustomerDTO>> getCustomersByCity(
            @Parameter(description = "City name")
            @PathVariable String city) {

        log.info("Fetching customers by city: {}", city);

        List<CustomerDTO> customers = customerService.getCustomersByCity(city).stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(customers);
    }
}