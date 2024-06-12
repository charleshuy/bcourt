package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.DTO.UserDTO;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.utils.ValidationUtil;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepo userRepo;

    public User createUser(User user) {
        validateUserFields(user); // Call the validation method
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        return userRepo.save(user);
    }
    @Transactional
    public User updateUser(String userId, User updatedUser) {
        Optional<User> existingUserOpt = userRepo.findById(userId);

        if (!existingUserOpt.isPresent()) {
            log.error("User with ID {} not found", userId);
            throw new RuntimeException("User not found");
        }

        User existingUser = existingUserOpt.get();

        // Validate the new email if it is being updated
        if (updatedUser.getEmail() != null && !ValidationUtil.isValidEmail(updatedUser.getEmail())) {
            log.error("Invalid email format: {}", updatedUser.getEmail());
            throw new RuntimeException("Invalid email format");
        }

        // Ensure the email is unique if it is being changed
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail()) && existsByEmail(updatedUser.getEmail())) {
            log.error("Email already in use: {}", updatedUser.getEmail());
            throw new RuntimeException("Email already in use");
        }

        // Validate the phone number if it is being updated
        if (updatedUser.getPhone() != null && !ValidationUtil.isValidPhoneNumber(updatedUser.getPhone())) {
            log.error("Invalid phone number: {}", updatedUser.getPhone());
            throw new RuntimeException("Phone number must be 10 digits");
        }

        // Update fields if they are provided
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getWalletAmount() != null) {
            existingUser.setWalletAmount(updatedUser.getWalletAmount());
        }
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }

        // Collections like courts, favorites, orders, and reviews should generally be handled separately
        // Update other fields as needed

        return userRepo.save(existingUser);
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

    public void deleteUserById(String userId) {
        Optional<User> existingUserOpt = userRepo.findById(userId);
        if (existingUserOpt.isPresent()) {
            userRepo.deleteById(userId);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }
    private void validateUserFields(User user) {
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!ValidationUtil.isValidPhoneNumber(user.getPhone())) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }
    }

}
