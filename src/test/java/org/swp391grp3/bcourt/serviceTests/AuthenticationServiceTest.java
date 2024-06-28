package org.swp391grp3.bcourt.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.swp391grp3.bcourt.dto.AuthenticationResponse;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.UserRepo;
import org.swp391grp3.bcourt.services.*;
import org.swp391grp3.bcourt.utils.ValidationUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @Mock
    private RoleService roleService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("Test User");
        user.setVerificationToken(UUID.randomUUID().toString());
        token = "jwt-token";
    }

    @Test
    void testLogin() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(token);

        AuthenticationResponse authResponse = authenticationService.login(user, response);

        assertNotNull(authResponse);
        assertEquals(token, authResponse.getToken());
        verify(response).addCookie(any(Cookie.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRegister() {
        when(userRepo.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded-password");
        when(jwtService.generateToken(user)).thenReturn(token);
        when(roleService.getRoleById("1")).thenReturn(new Role());

        AuthenticationResponse authResponse = authenticationService.register(user, response);

        assertNotNull(authResponse);
        assertEquals(token, authResponse.getToken());
        verify(userRepo).save(user);
        verify(emailService).sendEmail(eq(user.getEmail()), anyString(), anyString());
        verify(response).addCookie(any(Cookie.class));
    }

    // Additional tests for exception scenarios can be added here.
}