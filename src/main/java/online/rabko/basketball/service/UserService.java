package online.rabko.basketball.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import online.rabko.basketball.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Service interface for managing {@link User} entities.
 */
public interface UserService {

    /**
     * Retrieves all users.
     *
     * @return list of users.
     */
    List<User> findAll();

    /**
     * Retrieves a user by ID.
     *
     * @param id user ID.
     * @return found user.
     * @throws EntityNotFoundException if not found.
     */
    User findById(Long id);

    /**
     * Saves a user.
     *
     * @param user user entity.
     * @return saved user.
     */
    User save(User user);

    /**
     * Creates a new user.
     *
     * @param user user entity.
     * @return created user.
     * @throws EntityExistsException if username already exists.
     */
    User create(User user);

    /**
     * Updates an existing user.
     *
     * @param id          user ID.
     * @param replacement replacement entity.
     * @return updated user.
     * @throws EntityNotFoundException if not found.
     * @throws EntityExistsException   if username already exists.
     */
    User update(Long id, User replacement);

    /**
     * Deletes a user by ID.
     *
     * @param id user ID.
     * @throws EntityNotFoundException if not found.
     */
    void delete(Long id);

    /**
     * Retrieves a user by username.
     *
     * @param username username.
     * @return found user.
     * @throws UsernameNotFoundException if not found.
     */
    User getByUsername(String username);

    /**
     * Returns UserDetailsService backed by this service.
     *
     * @return UserDetailsService.
     */
    UserDetailsService userDetailsService();

    /**
     * Checks if username exists.
     *
     * @param username username.
     * @return true if exists, false otherwise.
     */
    boolean existsByUsername(String username);
}
