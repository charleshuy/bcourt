package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.RoleDTO;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.RoleRepo;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class RoleService {
    private final RoleRepo roleRepo;
    private final ModelMapper modelMapper;

    public Role createRole(Role role){
        return roleRepo.save(role);
    }
    public Page<RoleDTO> getAllRoles(int page, int size) {
        Page<Role> rolesPage = roleRepo.findAll(PageRequest.of(page, size, Sort.by("roleName")));
        return rolesPage.map(role -> {
            RoleDTO roleDTO = modelMapper.map(role, RoleDTO.class);
            List<String> userIds = role.getUsers().stream().map(User::getUserId).collect(Collectors.toList());
            roleDTO.setUserIds(userIds);
            return roleDTO;
        });
    }
    public Role getRoleById(String id) {
        return roleRepo.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
    }
    public void deleteRole(Role role){
        roleRepo.delete(role);
    }
    public void deleteRoleById(String id) {
        Role role = roleRepo.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        roleRepo.delete(role);
    }
}
