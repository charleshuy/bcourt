package org.swp391grp3.bcourt.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.AuthenticationResponse;
import org.swp391grp3.bcourt.dto.LoginDTO;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.services.AuthenticationService;
import org.swp391grp3.bcourt.services.UserService;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authService;
    private final UserRepo userRepo;

    @PostMapping("/loginNoJwt")
    public ResponseEntity<?> loginNoJwt(@RequestBody LoginDTO loginRequest) {
        Optional<User> userOpt = userService.loginWithEmail(loginRequest.getEmail(), loginRequest.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return ResponseEntity.ok().body(userService.userReturnToDTO(user));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request, HttpServletResponse response){
        return ResponseEntity.ok(authService.login(request, response));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(authService.register(request, response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthenticationResponse(null, "An unexpected error occurred. Please try again later."));
        }
    }


    @GetMapping("/verify")
    public void verifyEmail(@RequestParam("token") String token, HttpServletResponse response) throws IOException, IOException {
        Optional<User> userOptional = userRepo.findByVerificationToken(token);
        if (userOptional.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid verification token");
            return;
        }

        User user = userOptional.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepo.save(user);

        response.sendRedirect("http://localhost:5173/verify/success");
    }

    @PostMapping("/initiate-reset-password")
    public ResponseEntity<String> initiateResetPassword(@RequestBody String email) {
        authService.initiateResetPassword(email);
        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset successfully");
    }
}
