package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.PaymentMethodDTO;
import org.swp391grp3.bcourt.entities.Paymentmethod;
import org.swp391grp3.bcourt.services.PaymentService;

import java.net.URI;

@RestController
@RequestMapping("/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {
    private final PaymentService service;
    @PostMapping
    public ResponseEntity<Paymentmethod> createPaymentMethod(@RequestBody Paymentmethod method) {
        Paymentmethod createMethod = service.createPaymentmethod(method);
        URI location = URI.create("/payment-methods/" + createMethod.getMethodId());
        return ResponseEntity.created(location).body(createMethod);
    }
    @GetMapping()
    public ResponseEntity<Page<PaymentMethodDTO>> getAllPaymentMethods(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                       @RequestParam(value = "size", defaultValue = "10") int size){
        Page<Paymentmethod> methods = service.getAllPaymentMethod(page, size);
        return ResponseEntity.ok().body(service.methodDTOConverter(page, size, methods));
    }

    @DeleteMapping("delete/{methodId}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable String methodId) {
        try {
            service.deletePaymentMethodById(methodId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
