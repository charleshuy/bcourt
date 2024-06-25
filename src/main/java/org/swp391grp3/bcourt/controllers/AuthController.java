package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.AuthenticationResponse;
import org.swp391grp3.bcourt.dto.LoginDTO;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.services.AuthenticationService;
import org.swp391grp3.bcourt.services.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authService;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/loginNoJwt")
    public ResponseEntity<?> loginNoJwt(@RequestBody LoginDTO loginRequest) {
        Optional<User> userOpt = userService.loginWithEmail(loginRequest.getEmail(), loginRequest.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return ResponseEntity.ok().body(userService.userReturnToDTO(user)); // Return the whole User object
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request){
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request){
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        Optional<User> userOptional = userRepo.findByVerificationToken(token);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("Invalid verification token");
        }

        User user = userOptional.get();
        user.setEnabled(true);
        user.setVerificationToken(null); // Clear the verification token
        userRepo.save(user);

        return ResponseEntity.ok("Email verified successfully");
    }

}