package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.services.RoleService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role){
        return ResponseEntity.created(URI.create("/roles/roleId")).body(roleService.createRole(role));
    }
    @GetMapping("{id}")
    public ResponseEntity<Role> getRole(@PathVariable("id") UUID id){
        return ResponseEntity.ok().body(roleService.getRoleById(id));
    }
    @GetMapping
    public ResponseEntity<Page<Role>> getAllRole(@RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok().body(roleService.getAllRoles(page, size));
    }

}
