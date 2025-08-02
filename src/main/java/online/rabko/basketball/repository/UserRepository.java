package online.rabko.basketball.repository;

import java.util.Optional;
import online.rabko.basketball.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, or empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username already exists.
     *
     * @param username the username to check
     * @return true if a user exists with that username, false otherwise
     */
    boolean existsByUsername(String username);
}
