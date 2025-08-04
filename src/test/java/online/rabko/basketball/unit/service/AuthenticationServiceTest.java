package online.rabko.basketball.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import online.rabko.basketball.entity.User;
import online.rabko.basketball.enums.Role;
import online.rabko.basketball.exception.UserAlreadyExistsException;
import online.rabko.basketball.service.AuthenticationService;
import online.rabko.basketball.service.JwtService;
import online.rabko.basketball.service.UserService;
import online.rabko.model.JwtAuthenticationResponse;
import online.rabko.model.SignInRequest;
import online.rabko.model.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for {@link AuthenticationService}.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void signUp_shouldReturnJwtToken_whenUserIsNew() {
        SignUpRequest request = new SignUpRequest("testuser", "password");
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        User user = User.builder()
            .username("testuser")
            .password("encodedPassword")
            .role(Role.USER)
            .build();
        when(userService.create(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("mockJwtToken");
        JwtAuthenticationResponse response = authenticationService.signUp(request);
        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        verify(userService).create(any(User.class));
        verify(jwtService).generateToken(user);
    }

    @Test
    void signUp_shouldThrow_whenUserAlreadyExists() {
        SignUpRequest request = new SignUpRequest("existinguser", "password");
        when(userService.existsByUsername("existinguser")).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.signUp(request));
        verify(userService, never()).create(any(User.class));
    }

    @Test
    void signIn_shouldReturnJwtToken_whenCredentialsAreValid() {
        SignInRequest request = new SignInRequest("testuser", "password");
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.userDetailsService()).thenReturn(username -> userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mockJwtToken");
        JwtAuthenticationResponse response = authenticationService.signIn(request);
        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userDetails);
    }
}
