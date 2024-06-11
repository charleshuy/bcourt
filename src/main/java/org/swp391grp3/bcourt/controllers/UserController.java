package org.swp391grp3.bcourt.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.DTO.UserDTO;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.services.UserService;

import java.net.URI;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        String email = user.getEmail();
        if (userService.isValidEmail(email)){
            if (userService.existsByEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use.");
            }
            return ResponseEntity.created(URI.create("/users/" + user.getUserId())).body(userService.createUser(user));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is invalid.");
    }
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok().body(userService.getAllUsersReturnDTO(page, size));
    }

}
