package org.swp391grp3.bcourt.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.AuthenticationResponse;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.utils.ValidationUtil;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepo userRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RoleService roleService;


    public AuthenticationResponse login(User request, HttpServletResponse response ){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );
        User user = userRepo.findByEmail(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);

        // Create and add the cookie
        Cookie cookie = new Cookie("user-session", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);

        return new AuthenticationResponse(token);
    }
    public AuthenticationResponse register(User request, HttpServletResponse response) {
        userService.validateUserFields(request);
        request.setName(ValidationUtil.normalizeName(request.getName()));
        request.setWalletAmount(0.0);
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setEnabled(false); // Set enabled to false until email is verified
        request.setVerificationToken(UUID.randomUUID().toString()); // Generate a verification token
        Role role = roleService.getRoleById("1");
        request.setRole(role);
        request.setBanCount(1);
        userRepo.save(request);

        // Send verification email
        String verificationLink = "http://localhost:8080/auth/verify?token=" + request.getVerificationToken();
        emailService.sendEmail(request.getEmail(), "Email Verification", "Click the link to verify your email: " + verificationLink);

        // No JWT token generation here

        Cookie cookie = new Cookie("user-session", null);
        cookie.setMaxAge(0); // Expire cookie
        response.addCookie(cookie);

        return new AuthenticationResponse("User registered successfully. Please verify your email.");
    }
}
