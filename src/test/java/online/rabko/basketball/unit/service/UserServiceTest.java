package online.rabko.basketball.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.enums.Role;
import online.rabko.basketball.repository.UserRepository;
import online.rabko.basketball.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Unit tests for the {@link UserService}.
 */
class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void save_shouldSaveUser() {
        User user = new User(1L, "john", "pwd", Role.USER);
        when(userRepository.save(user)).thenReturn(user);
        User result = userService.save(user);
        assertEquals(user, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void create_shouldCreateUserWhenUsernameNotExists() {
        User user = new User(1L, "newuser", "pwd", Role.USER);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User created = userService.create(user);

        assertEquals(user, created);
        verify(userRepository).save(user);
    }

    @Test
    void create_shouldThrowWhenUserAlreadyExists() {
        User user = new User(1L, "existing", "pwd", Role.USER);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.create(user));
        assertEquals("User with this username already exists", exception.getMessage());
    }

    @Test
    void getByUsername_shouldReturnUser() {
        User user = new User(1L, "john", "pwd", Role.USER);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("john");

        assertEquals(user, result);
    }

    @Test
    void getByUsername_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername("unknown"));
    }

    @Test
    void userDetailsService_shouldReturnUserDetails() {
        User user = new User(1L, "john", "pwd", Role.USER);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername("john");
        assertEquals(user, userDetails);
    }

    @Test
    void existsByUsername_shouldReturnTrue() {
        when(userRepository.existsByUsername("check")).thenReturn(true);
        assertTrue(userService.existsByUsername("check"));
    }

    @Test
    void existsByUsername_shouldReturnFalse() {
        when(userRepository.existsByUsername("missing")).thenReturn(false);
        assertFalse(userService.existsByUsername("missing"));
    }
}
