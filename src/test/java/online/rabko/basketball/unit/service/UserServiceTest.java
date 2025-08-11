package online.rabko.basketball.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.repository.UserRepository;
import online.rabko.basketball.service.UserService;
import online.rabko.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Unit tests for {@link UserService}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void save_shouldPersistUser() {
        User user = User.builder()
            .id(1L)
            .username("john")
            .password("pwd")
            .role(Role.USER)
            .build();
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void create_shouldReturnUser_whenUsernameNotExists() {
        User user = User.builder()
            .id(1L)
            .username("john")
            .password("pwd")
            .role(Role.USER)
            .build();
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.create(user);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void create_shouldThrowException_whenUsernameAlreadyExists() {
        User user = User.builder()
            .id(1L)
            .username("john")
            .password("pwd")
            .role(Role.USER)
            .build();
        when(userRepository.existsByUsername("john")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.create(user));
        assertEquals("User with this username already exists", ex.getMessage());
    }

    @Test
    void getByUsername_shouldReturnUser_whenUserExists() {
        User user = User.builder()
            .id(1L)
            .username("john")
            .password("pwd")
            .role(Role.USER)
            .build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("john");

        assertEquals(user, result);
    }

    @Test
    void getByUsername_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername("ghost"));
    }

    @Test
    void userDetailsService_shouldReturnUserDetails_whenUserExists() {
        User user = User.builder()
            .id(1L)
            .username("john")
            .password("pwd")
            .role(Role.USER)
            .build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails details = userService.userDetailsService().loadUserByUsername("john");

        assertEquals(user, details);
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUserExists() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertTrue(userService.existsByUsername("john"));
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserNotExists() {
        when(userRepository.existsByUsername("ghost")).thenReturn(false);

        assertFalse(userService.existsByUsername("ghost"));
    }
}
