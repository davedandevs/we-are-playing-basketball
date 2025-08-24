package online.rabko.basketball.repository;

import java.util.Optional;
import online.rabko.basketball.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Spring Data REST repository for managing {@link User} via /users. Secured with role checks.
 */
@RepositoryRestResource(path = "users", collectionResourceRel = "users")
public interface UserRestRepository extends JpaRepository<User, Long> {

    /**
     * GET /users.
     */
    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @NonNull
    java.util.List<User> findAll();

    /**
     * GET /users/{id}.
     */
    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @NonNull
    Optional<User> findById(@NonNull Long id);

    /**
     * POST /users, PUT/PATCH /users/{id}.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @NonNull
    <S extends User> S save(@NonNull S entity);

    /**
     * DELETE /users/{id}.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    void deleteById(@NonNull Long id);

    /**
     * DELETE with entity.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    void delete(@NonNull User entity);

    /**
     * /users/search/findByUsername?etc.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    Optional<User> findByUsername(@NonNull String username);

    /**
     * existence check (admin-only).
     */
    @PreAuthorize("hasRole('ADMIN')")
    boolean existsByUsername(@NonNull String username);
}
