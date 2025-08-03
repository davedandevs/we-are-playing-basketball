package online.rabko.basketball.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.enums.Role;
import online.rabko.basketball.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for the {@link UserRepository}.
 */
public class UserRepositoryTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnUserWhenExistsByUsername() {
        User user = User.builder()
            .username("existingUser")
            .password("password")
            .role(Role.USER)
            .build();
        userRepository.save(user);
        Optional<User> found = userRepository.findByUsername(user.getUsername());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(found.get().getPassword()).isEqualTo(user.getPassword());
        assertThat(found.get().getRole()).isEqualTo(user.getRole());
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        assertThat(userRepository.findByUsername("nonexistentUser")).isEmpty();
    }

    @Test
    void shouldReturnTrueIfUsernameExists() {
        User user = User.builder()
            .username("checkUser")
            .password("pass123")
            .role(Role.USER)
            .build();
        userRepository.save(user);
        assertThat(userRepository.existsByUsername(user.getUsername())).isTrue();
    }

    @Test
    void shouldReturnFalseIfUsernameDoesNotExist() {
        assertThat(userRepository.existsByUsername("ghostUser")).isFalse();
    }
}
