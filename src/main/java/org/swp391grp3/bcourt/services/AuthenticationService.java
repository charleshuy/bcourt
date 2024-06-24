package org.swp391grp3.bcourt.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.swp391grp3.bcourt.dto.AuthenticationResponse;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.utils.ValidationUtil;

import java.util.Optional;

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

    public AuthenticationResponse login(User request ){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );
        User user = userRepo.findByEmail(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }
    public  AuthenticationResponse register(User request) {
        userService.validateUserFields(request);
        request.setName(ValidationUtil.normalizeName(request.getName()));
        request.setWalletAmount(0.0);
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(request);
        String token = jwtService.generateToken(request);
        return new AuthenticationResponse(token);
    }
}
