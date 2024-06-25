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

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody Order order) {
        Order createOrder = service.createOrder(order);
        URI location = URI.create("/orders/" + createOrder.getOrderId());
        return ResponseEntity.created(location).body(service.orderDTOConverter(createOrder));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrderDTO>> getAllOrdersByUserId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                                               @PathVariable String userId) {
        Page<Order> orders = service.getAllOrdersByUserId(page, size, userId);
        return ResponseEntity.ok().body(service.orderDTOConverter(page, size, orders));
    }
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteOrderById(@PathVariable String orderId) {
        try {
            service.deleteOrderById(orderId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
