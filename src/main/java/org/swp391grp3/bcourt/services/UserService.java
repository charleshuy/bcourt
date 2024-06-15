package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.UserDTO;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.utils.ValidationUtil;

import java.util.Optional;


@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    public User createUser(User user) {
        validateUserFields(user); // Call the validation method
        user.setName(ValidationUtil.normalizeName(user.getName())); // Normalize the name
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

        validateUserFields(updatedUser); // Call the validation method
        updatedUser.setName(ValidationUtil.normalizeName(updatedUser.getName())); // Normalize the name

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
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Collections like courts, favorites, orders, and reviews should generally be handled separately
        // Update other fields as needed

        return userRepo.save(existingUser);
    }
    public UserDTO userReturnToDTO(String userId, User user) {
        UserDTO updatedDTO = modelMapper.map(user, UserDTO.class);
        return updatedDTO;
    }

    public Page<User> getAllUsers(int page, int size) {
        return userRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }
    public Page<UserDTO> pageUserDTO(int page, int size, Page<User> userPage){
        return userPage.map(user -> modelMapper.map(user, UserDTO.class));
    }
    public Page<User> searchUsersByName(int page, int size, String userName) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepo.searchUserByName(userName, pageable);
        return usersPage;
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
        if (!ValidationUtil.isValidName(user.getName())) {
            throw new IllegalArgumentException("Name contains invalid characters");
        }
    }

}
