package online.rabko.basketball.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.repository.UserRepository;
import online.rabko.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for {@link UserRepository}.
 */
class UserRepositoryTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        User user = User.builder()
            .username("existingUser")
            .password("password")
            .role(Role.USER)
            .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("existingUser");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("existingUser");
        assertThat(found.get().getPassword()).isEqualTo("password");
        assertThat(found.get().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<User> found = userRepository.findByUsername("nonexistentUser");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUserExists() {
        User user = User.builder()
            .username("checkUser")
            .password("pass123")
            .role(Role.USER)
            .build();

        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("checkUser");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserDoesNotExist() {
        boolean exists = userRepository.existsByUsername("ghostUser");
        assertThat(exists).isFalse();
    }
}
