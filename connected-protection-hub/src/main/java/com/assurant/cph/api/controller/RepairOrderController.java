package com.assurant.cph.api.controller;

import com.assurant.cph.core.domain.RepairOrder;
import com.assurant.cph.core.service.RepairOrderService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/repair-orders")
@RequiredArgsConstructor
@Tag(name = "Repair Orders", description = "Repair order management APIs")
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    @PostMapping
    @Operation(summary = "Create repair order", description = "Creates a new repair order for a claim")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Repair order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Claim not found")
    })
    public ResponseEntity<RepairOrder> createRepairOrder(
            @Parameter(description = "Repair order data")
            @Valid @RequestBody RepairOrder repairOrder) {

        log.info("Creating repair order for claim: {}", repairOrder.getClaim().getId());

        RepairOrder createdOrder = repairOrderService.createRepairOrder(repairOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping
    @Operation(summary = "Get all repair orders", description = "Retrieves a list of all repair orders")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all repair orders")
    public ResponseEntity<List<RepairOrder>> getAllRepairOrders() {
        log.info("Fetching all repair orders");
        List<RepairOrder> orders = repairOrderService.getAllRepairOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get repair order by ID", description = "Retrieves a specific repair order by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repair order found"),
            @ApiResponse(responseCode = "404", description = "Repair order not found")
    })
    public ResponseEntity<RepairOrder> getRepairOrderById(
            @Parameter(description = "Repair order ID")
            @PathVariable UUID id) {

        log.info("Fetching repair order by ID: {}", id);

        Optional<RepairOrder> order = repairOrderService.getRepairOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/claim/{claimId}")
    @Operation(summary = "Get repair order by claim", description = "Retrieves repair order for a specific claim")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repair order found"),
            @ApiResponse(responseCode = "404", description = "Repair order not found for this claim")
    })
    public ResponseEntity<RepairOrder> getRepairOrderByClaimId(
            @Parameter(description = "Claim ID")
            @PathVariable UUID claimId) {

        log.info("Fetching repair order for claim: {}", claimId);

        Optional<RepairOrder> order = repairOrderService.getRepairOrderByClaimId(claimId);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get repair orders by status", description = "Retrieves all repair orders with a specific status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved repair orders")
    public ResponseEntity<List<RepairOrder>> getRepairOrdersByStatus(
            @Parameter(description = "Repair status")
            @PathVariable RepairOrder.RepairStatus status) {

        log.info("Fetching repair orders with status: {}", status);

        List<RepairOrder> orders = repairOrderService.getRepairOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update repair order status", description = "Updates the status of a specific repair order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repair order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Repair order not found")
    })
    public ResponseEntity<RepairOrder> updateRepairOrderStatus(
            @Parameter(description = "Repair order ID") @PathVariable UUID id,
            @Parameter(description = "New status") @RequestParam RepairOrder.RepairStatus status) {

        log.info("Updating repair order status for ID: {} to {}", id, status);

        RepairOrder updatedOrder = repairOrderService.updateRepairOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Complete repair order", description = "Marks a repair order as completed with completion details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Repair order completed successfully"),
            @ApiResponse(responseCode = "404", description = "Repair order not found")
    })
    public ResponseEntity<RepairOrder> completeRepairOrder(
            @Parameter(description = "Repair order ID") @PathVariable UUID id,
            @Parameter(description = "Final repair cost") @RequestParam Double repairCost,
            @Parameter(description = "Parts replaced") @RequestParam String partsReplaced) {

        log.info("Completing repair order with ID: {}", id);

        RepairOrder completedOrder = repairOrderService.completeRepairOrder(id, repairCost, partsReplaced);
        return ResponseEntity.ok(completedOrder);
    }

    @GetMapping("/provider/{provider}")
    @Operation(summary = "Get repair orders by provider", description = "Retrieves all repair orders for a specific provider")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved repair orders")
    public ResponseEntity<List<RepairOrder>> getRepairOrdersByProvider(
            @Parameter(description = "Repair provider name")
            @PathVariable String provider) {

        log.info("Fetching repair orders for provider: {}", provider);

        List<RepairOrder> orders = repairOrderService.getRepairOrdersByProvider(provider);
        return ResponseEntity.ok(orders);
    }
}