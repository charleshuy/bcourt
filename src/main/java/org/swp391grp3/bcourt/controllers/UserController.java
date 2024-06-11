package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.created(URI.create("/users/userId")).body(userService.createUser(user));
    }
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok().body(userService.getAllUsersReturnDTO(page, size));
    }

}
