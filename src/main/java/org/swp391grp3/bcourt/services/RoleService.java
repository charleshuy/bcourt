package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.repo.RoleRepo;

import java.util.List;
import java.util.UUID;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class RoleService {
    private final RoleRepo roleRepo;
    public Role createRole(Role role){
        role.setRoleId(UUID.randomUUID());
        return roleRepo.save(role);
    }
    public List<Role> getRoleList() {
        return roleRepo.findAll();
    }
    public Page<Role> getAllRoles(int page, int size) {
        return roleRepo.findAll(PageRequest.of(page, size, Sort.by("roleName")));
    }
    public Role getRoleById(UUID id) {
        return roleRepo.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
    }
    public void deleteRole(Role role){
        roleRepo.delete(role);
    }
}
