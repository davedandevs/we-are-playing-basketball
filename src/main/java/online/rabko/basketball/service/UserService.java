package online.rabko.basketball.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for the {@link User} entity.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    /**
     * Retrieves all users.
     *
     * @return list of users.
     */
    public List<User> findAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id user ID.
     * @return found user.
     * @throws EntityNotFoundException if not found.
     */
    public User findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    /**
     * Saves a user.
     *
     * @param user user entity.
     * @return saved user.
     */
    public User save(User user) {
        return repository.save(user);
    }

    /**
     * Creates a new user.
     *
     * @param user user entity.
     * @return created user.
     * @throws EntityExistsException if username already exists.
     */
    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new EntityExistsException("User with this username already exists");
        }
        return save(user);
    }

    /**
     * Updates an existing user.
     *
     * @param id          user ID.
     * @param replacement replacement entity.
     * @return updated user.
     * @throws EntityNotFoundException if not found.
     * @throws EntityExistsException   if username already exists.
     */
    public User update(Long id, User replacement) {
        User existing = findById(id);
        if (!existing.getUsername().equals(replacement.getUsername())
            && repository.existsByUsername(replacement.getUsername())) {
            throw new EntityExistsException("User with this username already exists");
        }
        replacement.setId(id);
        return repository.save(replacement);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id user ID.
     * @throws EntityNotFoundException if not found.
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("User not found: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Retrieves a user by username.
     *
     * @param username username.
     * @return found user.
     * @throws UsernameNotFoundException if not found.
     */
    public User getByUsername(String username) {
        return repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Returns UserDetailsService backed by this service.
     *
     * @return UserDetailsService.
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Checks if username exists.
     *
     * @param username username.
     * @return true if exists, false otherwise.
     */
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
