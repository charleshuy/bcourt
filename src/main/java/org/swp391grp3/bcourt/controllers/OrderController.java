package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.OrderDTO;
import org.swp391grp3.bcourt.entities.Order;
import org.swp391grp3.bcourt.services.OrderService;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = service.createOrder(order);
            URI location = URI.create("/orders/" + createdOrder.getOrderId());
            return ResponseEntity.created(location).body(service.orderDTOConverter(createdOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the order.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<Order> orders = service.getAllOrders(page, size);
            return ResponseEntity.ok().body(service.orderDTOConverter(page, size, orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching orders.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllOrdersByUserId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                  @PathVariable String userId) {
        try {
            Page<Order> orders = service.getAllOrdersByUserId(page, size, userId);
            return ResponseEntity.ok().body(service.orderDTOConverter(page, size, orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching orders by user.");
        }
    }

    @GetMapping("/court/{courtId}")
    public ResponseEntity<?> getAllOrdersByCourtId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                                   @PathVariable String courtId) {
        try {
            Page<Order> orders = service.getOrdersByCourtId(courtId, page, size);
            return ResponseEntity.ok().body(service.orderDTOConverter(page, size, orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching orders by court.");
        }
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteOrderById(@PathVariable String orderId) {
        try {
            service.deleteOrderById(orderId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the order.");
        }
    }

    @GetMapping("/court/{courtId}/date/{bookingDate}")
    public ResponseEntity<?> getOrdersByCourtAndDate(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                     @PathVariable String courtId,
                                                     @PathVariable String bookingDate) {
        try {
            LocalDate date = LocalDate.parse(bookingDate);
            Page<Order> orders = service.getOrdersByCourtAndDate(courtId, date, page, size);
            return ResponseEntity.ok().body(service.orderDTOConverter(page, size, orders));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching orders by court and date.");
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable String orderId, @RequestBody Order updatedOrder) {
        try {
            Order updated = service.updateOrder(orderId, updatedOrder);
            return ResponseEntity.ok(service.orderDTOConverter(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the order.");
        }
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId, @RequestParam String userId) {
        try {
            System.out.println(orderId + userId);
            service.cancelOrder(orderId, userId);
            return ResponseEntity.ok("Order cancelled successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while cancelling the order.");
        }
    }
    @PutMapping("/refund/{orderId}")
    public ResponseEntity<?> refundForEWalletOrder(@PathVariable String orderId) {
        try {
            service.refundForEWalletOrder(orderId);
            return ResponseEntity.ok("Order refunded successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the refund.");
        }
    }
}
