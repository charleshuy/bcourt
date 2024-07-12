package org.swp391grp3.bcourt.serviceTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swp391grp3.bcourt.entities.Paymentmethod;
import org.swp391grp3.bcourt.repo.PaymentRepo;
import org.swp391grp3.bcourt.services.PaymentService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepo repo;

    @InjectMocks
    private PaymentService service;

    @Test
    void createPaymentMethod_Success() {
        Paymentmethod method = new Paymentmethod();
        when(repo.save(method)).thenReturn(method);

        Paymentmethod createPaymentmethod = service.createPaymentmethod(method);
        assertNotNull(createPaymentmethod);
        assertEquals(method, createPaymentmethod);
        verify(repo, times(1)).save(method);
    }
}
