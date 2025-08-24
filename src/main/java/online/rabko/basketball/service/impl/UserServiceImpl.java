package online.rabko.basketball.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.repository.UserRepository;
import online.rabko.basketball.service.UserService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for the {@link User} entity.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        return repository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new EntityExistsException("User with this username already exists");
        }
        return save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("User not found: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByUsername(String username) {
        return repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
