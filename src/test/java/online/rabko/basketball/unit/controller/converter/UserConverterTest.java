package online.rabko.basketball.unit.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import online.rabko.basketball.controller.converter.UserConverter;
import online.rabko.basketball.entity.User;
import online.rabko.model.Role;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link UserConverter}.
 */
class UserConverterTest {

    private final UserConverter converter = new UserConverter();

    @Test
    void convert_shouldMapAllFields_withRole() {
        User entity = User.builder()
            .id(1L)
            .username("john")
            .password("secret")
            .firstName("John")
            .lastName("Doe")
            .role(Role.ADMIN)
            .build();

        online.rabko.model.User dto = converter.convert(entity);

        assertEquals(1L, dto.getId());
        assertEquals("john", dto.getUsername());
        assertEquals("secret", dto.getPassword());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals(Role.ADMIN, dto.getRole());
    }

    @Test
    void convert_shouldHandleNullRole() {
        User entity = User.builder()
            .id(2L)
            .username("noRoleUser")
            .password("pwd")
            .firstName("No")
            .lastName("Role")
            .role(null)
            .build();

        online.rabko.model.User dto = converter.convert(entity);

        assertNull(dto.getRole());
    }

    @Test
    void convertBack_shouldMapAllFields_withRole() {
        online.rabko.model.User dto = new online.rabko.model.User()
            .id(3L)
            .username("alice")
            .password("pass")
            .firstName("Alice")
            .lastName("Smith")
            .role(Role.USER);

        User entity = converter.convertBack(dto);

        assertEquals("alice", entity.getUsername());
        assertEquals("pass", entity.getPassword());
        assertEquals("Alice", entity.getFirstName());
        assertEquals("Smith", entity.getLastName());
        assertEquals(Role.USER, entity.getRole());
    }

    @Test
    void convertBack_shouldHandleNullRole() {
        online.rabko.model.User dto = new online.rabko.model.User()
            .username("nullRoleUser")
            .password("pass")
            .firstName("Null")
            .lastName("Role")
            .role(null);

        User entity = converter.convertBack(dto);

        assertNull(entity.getRole());
    }
}
