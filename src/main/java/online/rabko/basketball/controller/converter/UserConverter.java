package online.rabko.basketball.controller.converter;

import online.rabko.basketball.entity.User;
import org.springframework.stereotype.Component;

/**
 * Converts between entity {@link User} and DTO {@link online.rabko.model.User}.
 */
@Component
public class UserConverter extends TwoWayConverter<User, online.rabko.model.User> {

    /**
     * {@inheritDoc}
     */
    @Override
    public online.rabko.model.User convert(User source) {
        return new online.rabko.model.User()
            .id(source.getId())
            .username(source.getUsername())
            .password(source.getPassword())
            .firstName(source.getFirstName())
            .lastName(source.getLastName())
            .role(source.getRole() != null ? online.rabko.model.Role.fromValue(
                source.getRole().getValue()) : null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User convertBack(online.rabko.model.User dto) {
        return User.builder()
            .username(dto.getUsername())
            .password(dto.getPassword())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .role(dto.getRole() != null ? online.rabko.model.Role.ADMIN.equals(dto.getRole())
                ? online.rabko.model.Role.ADMIN : online.rabko.model.Role.USER : null)
            .build();
    }
}
