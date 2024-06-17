package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391grp3.bcourt.dto.LoginDTO;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.services.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest) {
        Optional<User> userOpt = userService.loginWithEmail(loginRequest.getEmail(), loginRequest.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return ResponseEntity.ok().body(userService.userReturnToDTO(user)); // Return the whole User object
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}
