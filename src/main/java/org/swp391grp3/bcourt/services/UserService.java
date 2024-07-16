package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.UserDTO;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.FileData;
import org.swp391grp3.bcourt.entities.Order;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.CourtRepo;
import org.swp391grp3.bcourt.repo.OrderRepo;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.utils.ValidationUtil;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class UserService {
    private final OrderRepo orderRepo;

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final CourtRepo courtRepo;
    private final FileService fileService;

    public User createUser(User user) {
        validateUserFields(user); // Call the validation method
        user.setName(ValidationUtil.normalizeName(user.getName())); // Normalize the name

        user.setRefundWallet(0.0);
        user.setBanCount(0);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        if (userRepo.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        user.setEnabled(true);
        return userRepo.save(user);
    }

    @Transactional
    public User updateUser(User updatedUser) {
        String userId = updatedUser.getUserId();
        Optional<User> existingUserOpt = userRepo.findById(userId);

        if (!existingUserOpt.isPresent()) {
            log.error("User with ID {} not found", userId);
            throw new RuntimeException("User not found");
        }

        User existingUser = existingUserOpt.get();

        // Update fields if they are provided
        if (updatedUser.getName() != null) {
            updatedUser.setName(ValidationUtil.normalizeName(updatedUser.getName())); // Normalize the name
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getWalletAmount() != null) {
            existingUser.setWalletAmount(updatedUser.getWalletAmount());
        }
        if (updatedUser.getRefundWallet() != null) {
            existingUser.setRefundWallet(updatedUser.getRefundWallet());
        }
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());

            // If the role is not "staff", set manager to null
            if (!updatedUser.getRole().getRoleId().equalsIgnoreCase("4")) {
                existingUser.setManager(null);
            } else if (updatedUser.getManager() != null){
                // Update the manager if the role is "staff"
                existingUser.setManager(updatedUser.getManager());
            }
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getAssignedCourt() != null){
            existingUser.setAssignedCourt(updatedUser.getAssignedCourt());
        }


        // Validate updated user fields
        validateUserUpdateFields(existingUser);

        return userRepo.save(existingUser);
    }

    public void updatePassword(User updatedUser, String oldPassword){
        String userId = updatedUser.getUserId();
        Optional<User> existingUserOpt = userRepo.findById(userId);

        if (!existingUserOpt.isPresent()) {
            log.error("User with ID {} not found", userId);
            throw new RuntimeException("User not found");
        }
        User existingUser = existingUserOpt.get();
        if (!ValidationUtil.isValidPassword(updatedUser.getPassword())) {
            throw new IllegalArgumentException("Password must be 8-16 characters long");
        }
        String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
        existingUser.setPassword(encodedPassword);
        userRepo.save(existingUser);
    }

    public UserDTO userReturnToDTO(User user) {
        UserDTO updatedDTO = modelMapper.map(user, UserDTO.class);
        return updatedDTO;
    }

    public Page<User> getAllUsers(int page, int size) {
        return userRepo.findAll(PageRequest.of(page, size));
    }

    public Page<UserDTO> pageUserDTO(Page<User> userPage){
        return userPage.map(user -> modelMapper.map(user, UserDTO.class));
    }

    public User getUserById(String userId){
        return userRepo.findByUserId(userId);
    }

    public Page<User> searchUsersByName(int page, int size, String userName) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepo.searchUserByName(userName, pageable);
        return usersPage;
    }

    public void deleteUserById(String userId) {
        Optional<User> existingUserOpt = userRepo.findById(userId);
        if (existingUserOpt.isPresent()) {
            List<Court> courts = courtRepo.findByUser_UserId(userId);
            for (Court c : courts){
                c.setUser(null);
            }
            List<User> users = userRepo.findByManager_UserId(userId);
            for(User u : users){
                u.setManager(null);
            }
            List<Order> orders = orderRepo.findByUser_UserId(userId);
            orderRepo.deleteAll(orders);
            User user = existingUserOpt.get();
            fileService.deleteOldFile(user.getFile());
            userRepo.deleteById(userId);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public void validateUserUpdateFields(User user) {
        if (user.getEmail() != null && !ValidationUtil.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getPhone() != null && !ValidationUtil.isValidPhoneNumber(user.getPhone())) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }
        if (user.getName() != null && !ValidationUtil.isValidName(user.getName())) {
            throw new IllegalArgumentException("Name contains invalid characters");
        }
    }

    public void validateUserFields(User user) {
        if (!ValidationUtil.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!ValidationUtil.isValidPhoneNumber(user.getPhone())) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }
        if (!ValidationUtil.isValidName(user.getName())) {
            throw new IllegalArgumentException("Name contains invalid characters");
        }
        if (!ValidationUtil.isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("Password must be 8-16 characters long");
        }
    }

    public Optional<User> loginWithEmail(String email, String password) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (password.matches(user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    public void updateUserImg(String userId, FileData fileData){
        User user = userRepo.findByUserId(userId);
        user.setFile(fileData);
    }

    public Page<UserDTO> getUsersByManagerId(String managerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepo.findByManagerId(managerId, pageable);
        return usersPage.map(user -> modelMapper.map(user, UserDTO.class));
    }


    public void disableUserIfBanned(User user) {
        if (user.getBanCount() > 4 && user.isEnabled()) {
            user.setEnabled(false);
            userRepo.save(user);
            log.info("User {} has been disabled due to ban count exceeding 3.", user.getUserId());
        }
    }
    public Page<UserDTO> getUsersByRoleName(String roleName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepo.getUsersByRoleName(roleName, pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

}
