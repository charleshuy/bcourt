package org.swp391grp3.bcourt.configs;

import org.swp391grp3.bcourt.configs.VNPayService;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private VNPayService vnPayService;

    public String initiateDeposit(String userId, int amount, String orderInfo, String returnUrl) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return vnPayService.createOrder(amount, orderInfo, returnUrl);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public boolean handlePaymentReturn(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);
        if (paymentStatus == 1) {
            String userId = request.getParameter("vnp_OrderInfo"); // assuming you passed userId in orderInfo
            String amountStr = request.getParameter("vnp_Amount");
            double amount = Double.parseDouble(amountStr) / 100; // Convert to original amount
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setWalletAmount(user.getWalletAmount() + amount);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}