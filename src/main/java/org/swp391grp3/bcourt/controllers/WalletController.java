package org.swp391grp3.bcourt.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.swp391grp3.bcourt.configs.payment.WalletService;

import java.io.IOException;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("deposit")
    public String deposit(@RequestParam("userId") String userId,
                          @RequestParam("amount") int amount,
                          @RequestParam("orderInfo") String orderInfo,
                          HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String returnUrl = baseUrl + "/wallet/paymentReturn";
        String vnpayUrl = walletService.initiateDeposit(userId, amount, orderInfo, returnUrl);
        return  vnpayUrl;
    }

    @GetMapping("/paymentReturn")
    public void paymentReturn(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
        boolean success = walletService.handlePaymentReturn(request);
        if (success) {
            // Redirect to your React app
            response.sendRedirect("http://localhost:5173/success");  // Redirect to the root URL of your React app
        } else {
            // Handle failure redirection as needed
            response.sendRedirect("http://localhost:5173/error"); // Redirect to an error page
        }
    }
}