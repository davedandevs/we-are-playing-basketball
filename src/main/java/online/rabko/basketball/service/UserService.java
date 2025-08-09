package online.rabko.basketball.service;

import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for the User entity.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    /**
     * Saves the given user to the database.
     *
     * @param user the user entity to save
     * @return the saved user entity
     */
    public User save(User user) {
        return repository.save(user);
    }

    /**
     * Creates a new user if the username is not already taken. Throws an exception if a user with
     * the same username exists.
     *
     * @param user the user to create
     * @return the newly created user
     * @throws RuntimeException if a user with the given username already exists
     */
    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with this username already exists");
        }
        return save(user);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to look up
     * @return the user associated with the given username
     * @throws UsernameNotFoundException if no user is found
     */
    public User getByUsername(String username) {
        return repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Returns a {@link UserDetailsService} implementation that loads users by username. Required by
     * Spring Security.
     *
     * @return a UserDetailsService backed by this service
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Checks if a user with the given username already exists.
     *
     * @param username the username to check
     * @return true if a user exists with that username, false otherwise
     */
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
