package org.swp391grp3.bcourt.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${react.app.success.url}")
    private String successUrl;

    @Value("${react.app.error.url}")
    private String errorUrl;

    @PostMapping("deposit")
    public String deposit(@RequestParam("userId") String userId,
                          @RequestParam("amount") int amount,
                          @RequestParam("orderInfo") String orderInfo,
                          HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String returnUrl = baseUrl + "/wallet/paymentReturn";
        return walletService.initiateDeposit(userId, amount, orderInfo, returnUrl);
    }

    @GetMapping("/paymentReturn")
    public void paymentReturn(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
        boolean success = walletService.handlePaymentReturn(request);
        if (success) {
            // Redirect to your React app
            response.sendRedirect(successUrl);  // Redirect to the root URL of your React app
        } else {
            // Handle failure redirection as needed
            response.sendRedirect(errorUrl); // Redirect to an error page
        }
    }
}