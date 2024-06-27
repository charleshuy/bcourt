package org.swp391grp3.bcourt.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.AuthenticationResponse;
import org.swp391grp3.bcourt.dto.LoginDTO;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.services.AuthenticationService;
import org.swp391grp3.bcourt.services.RoleService;
import org.swp391grp3.bcourt.services.UserService;

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
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request, HttpServletResponse response){
        return ResponseEntity.ok(authService.register(request, response));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        Optional<User> userOptional = userRepo.findByVerificationToken(token);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid verification token");
        }

        User user = userOptional.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepo.save(user);

        return ResponseEntity.ok("Email verified successfully");
    }
}
