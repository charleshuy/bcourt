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
    public ResponseEntity<OrderDTO> createOrder(@RequestBody Order order) {
        Order createOrder = service.createOrder(order);
        URI location = URI.create("/courts/" + createOrder.getOrderId());
        return ResponseEntity.created(location).body(service.orderDTOConverter(createOrder));
    }
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size){
        Page<Order> orders = service.getAllOrders(page, size);
        return ResponseEntity.ok().body(service.orderDTOConverter(page, size, orders));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrderDTO>> getAllOrdersByUserId(@RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                                               @PathVariable String userId) {
        Page<Order> orders = service.getAllOrdersByUserId(page, size, userId);
        return ResponseEntity.ok().body(service.orderDTOConverter(page, size, orders));
    }
    @DeleteMapping("delete/{orderId}")
    public ResponseEntity<?> deleteOrderById(@PathVariable String orderId) {
        try {
            service.deleteOrder(orderId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/court/{courtId}/date/{bookingDate}")
    public ResponseEntity<Page<OrderDTO>> getOrdersByCourtAndDate(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                  @RequestParam(value = "size", defaultValue = "10") int size,
                                                                  @PathVariable String courtId,
                                                                  @PathVariable String bookingDate) {
        try {
            LocalDate date = LocalDate.parse(bookingDate);
            Page<Order> orders = service.getOrdersByCourtAndDate(courtId, date, page, size);
            return ResponseEntity.ok().body(service.orderDTOConverter(page, size, orders));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null); // Handle invalid date format
        }
    }
}
