package org.swp391grp3.bcourt.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swp391grp3.bcourt.entities.Role;
import org.swp391grp3.bcourt.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.swp391grp3.bcourt.services.JwtService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private String token;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@test.com");
        user.setUserId("1");
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        user.setRole(role);

        token = jwtService.generateToken(user);
    }

    @Test
    public void testExtractUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("test@test.com", username);
    }

    @Test
    public void testGenerateToken() {
        assertNotNull(token);
        assertTrue(!token.isEmpty());
    }

    @Test
    public void testIsValid() {
        when(userDetails.getUsername()).thenReturn("test@test.com");
        assertTrue(jwtService.isValid(token, userDetails));
    }


}
