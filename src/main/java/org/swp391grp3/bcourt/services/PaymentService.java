package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.PaymentMethodDTO;
import org.swp391grp3.bcourt.entities.Paymentmethod;
import org.swp391grp3.bcourt.repo.PaymentRepo;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final ModelMapper modelMapper;

    public Paymentmethod createPaymentmethod(Paymentmethod paymentmethod){
        return paymentRepo.save(paymentmethod);
    }
    public Page<Paymentmethod> getAllPaymentMethod(int page, int size){
        return paymentRepo.findAll(PageRequest.of(page, size, Sort.by("methodName")));
    }
    public Page<PaymentMethodDTO> methodDTOConverter(int page, int size, Page<Paymentmethod> methods){
        return methods.map(m -> modelMapper.map(m, PaymentMethodDTO.class));
    }
    public void deletePaymentMethodById(String id) {
        Paymentmethod method = paymentRepo.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        paymentRepo.delete(method);
    }
}
