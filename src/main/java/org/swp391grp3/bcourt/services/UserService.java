package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.DTO.UserDTO;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class UserService
{

    private final UserRepo userRepo;
    public User createUser(User role){
        return userRepo.save(role);
    }
    public Page<User> getAllUsers(int page, int size) {
        return userRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setRole(user.getRole().getRoleName());
        // Set other fields as needed
        return dto;
    }
    public Page<UserDTO> getAllUsersReturnDTO(int page, int size) {
        Page<User> usersPage = userRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
        return usersPage.map(this::convertToDTO);
    }
    public void deleteUser(User user) {
        userRepo.delete(user);
    }
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    private final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

}
