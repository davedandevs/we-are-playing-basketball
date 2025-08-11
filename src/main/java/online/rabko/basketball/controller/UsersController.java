package online.rabko.basketball.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.rabko.api.UsersApi;
import online.rabko.basketball.controller.converter.UserConverter;
import online.rabko.basketball.service.UserService;
import online.rabko.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for {@code /users} endpoints. Implements {@link UsersApi}.
 */
@RestController
@RequiredArgsConstructor
public class UsersController implements UsersApi {

    private final UserService userService;
    private final UserConverter converter;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<User>> usersGet() {
        return ResponseEntity.ok(
            userService.findAll().stream()
                .map(converter::convert)
                .toList()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<User> usersIdGet(Long id) {
        return ResponseEntity.ok(converter.convert(userService.findById(id)));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> usersIdPut(Long id, User dto) {
        dto.setId(null);
        online.rabko.basketball.entity.User toUpdate = converter.convertBack(dto);
        toUpdate.setId(id);
        online.rabko.basketball.entity.User updated = userService.update(id, toUpdate);
        return ResponseEntity.ok(converter.convert(updated));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> usersIdDelete(Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
