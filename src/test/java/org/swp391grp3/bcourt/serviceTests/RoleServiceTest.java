package org.swp391grp3.bcourt.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.swp391grp3.bcourt.DTO.RoleDTO;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.repo.RoleRepo;
import org.swp391grp3.bcourt.services.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRole_Success() {
        Role role = new Role();
        when(roleRepo.save(role)).thenReturn(role);

        Role createdRole = roleService.createRole(role);

        assertNotNull(createdRole);
        assertEquals(role, createdRole);
        verify(roleRepo, times(1)).save(role);
    }

    @Test
    void getAllRoles_Success() {
        List<Role> roleList = new ArrayList<>();
        roleList.add(new Role());
        roleList.add(new Role());
        roleList.add(new Role());

        // Mock the behavior of roleRepo.findAll() to return the roleList
        when(roleRepo.findAll()).thenReturn(roleList);

        Page<RoleDTO> rolePage = roleService.getAllRoles(0, 10);

        assertNotNull(rolePage);
        assertEquals(roleList.size(), rolePage.getContent().size());

        // Verify that roleRepo.findAll() is called once
        verify(roleRepo, times(1)).findAll();
    }



    @Test
    void getRoleById_Success() {
        String roleId = "1";
        Role role = new Role();
        role.setRoleId(roleId);

        when(roleRepo.findById(roleId)).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleById(roleId);

        assertNotNull(foundRole);
        assertEquals(roleId, foundRole.getRoleId());
        verify(roleRepo, times(1)).findById(roleId);
    }

    @Test
    void getRoleById_RoleNotFound() {
        String roleId = "1";

        when(roleRepo.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.getRoleById(roleId));

        verify(roleRepo, times(1)).findById(roleId);
    }

    @Test
    void deleteRole_Success() {
        Role role = new Role();

        assertDoesNotThrow(() -> roleService.deleteRole(role));
        verify(roleRepo, times(1)).delete(role);
    }
}
