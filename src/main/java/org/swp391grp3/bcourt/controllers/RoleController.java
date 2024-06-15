package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391grp3.bcourt.dto.RoleDTO;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.services.RoleService;

import java.net.URI;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        // Assuming the createdRole has a roleId property
        URI location = URI.create("/roles/" + createdRole.getRoleId());
        return ResponseEntity.created(location).body(createdRole);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable String id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<Page<RoleDTO>> getAllRoles(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<RoleDTO> roleDTOPage = roleService.getAllRoles(page, size);
        return ResponseEntity.ok(roleDTOPage);
    }
}
