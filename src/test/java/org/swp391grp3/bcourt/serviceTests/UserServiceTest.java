package org.swp391grp3.bcourt.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.services.UserService;
import org.swp391grp3.bcourt.utils.ValidationUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_whenValidUser_shouldSaveUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPhone("1234567890");

        when(userRepo.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepo.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void createUser_whenEmailAlreadyExists_shouldThrowException() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepo.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }
    @Test
    public void getAllUsers_whenValidRequest_shouldReturnUsersPage() {
        int page = 0;
        int size = 10;

        List<User> users = Arrays.asList(
                new User(),
                new User()
        );
        Page<User> usersPage = new PageImpl<>(users, PageRequest.of(page, size, Sort.by("name")), users.size());

        when(userRepo.findAll(any(Pageable.class))).thenReturn(usersPage);

        Page<User> result = userService.getAllUsers(page, size);

        assertEquals(usersPage, result);
        verify(userRepo, times(1)).findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    @Test
    void updateUser_whenValidUser_shouldUpdateUser() {
        String userId = "1";
        User existingUser = new User();
        existingUser.setEmail("old@example.com");
        User updatedUser = new User();
        updatedUser.setEmail("new@example.com");

        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepo.existsByEmail(updatedUser.getEmail())).thenReturn(false);
        when(userRepo.save(existingUser)).thenReturn(updatedUser); // Return the updated user after save

        User result = userService.updateUser(userId, updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getEmail(), result.getEmail());
        verify(userRepo, times(1)).findById(userId);
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    void updateUser_whenEmailInvalid_shouldThrowException() {
        String userId = "1";
        User existingUser = new User();
        existingUser.setEmail("old@example.com");
        User updatedUser = new User();
        updatedUser.setEmail("invalid-email");

        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> userService.updateUser(userId, updatedUser));
    }

    @Test
    void deleteUserById_whenUserExists_shouldDeleteUser() {
        String userId = "1";
        User existingUser = new User();

        when(userRepo.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepo).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepo, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserById_whenUserDoesNotExist_shouldThrowException() {
        String userId = "1";

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserById(userId));
    }
}