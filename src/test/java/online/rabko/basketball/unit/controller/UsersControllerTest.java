package online.rabko.basketball.unit.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.List;
import online.rabko.basketball.controller.UsersController;
import online.rabko.basketball.controller.converter.UserConverter;
import online.rabko.basketball.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Unit tests for {@link UsersController}.
 */
@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserConverter converter;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(usersController);
    }

    @Test
    void usersGet_shouldReturnList() {
        online.rabko.basketball.entity.User u1 = online.rabko.basketball.entity.User.builder()
            .id(1L).username("alice").build();
        online.rabko.basketball.entity.User u2 = online.rabko.basketball.entity.User.builder()
            .id(2L).username("bob").build();
        when(userService.findAll()).thenReturn(List.of(u1, u2));
        when(converter.convert(u1)).thenReturn(new online.rabko.model.User());
        when(converter.convert(u2)).thenReturn(new online.rabko.model.User());

        given()
            .when()
            .get("/users")
            .then()
            .statusCode(200)
            .body("$", hasSize(2));

        verify(userService).findAll();
        verify(converter, times(2)).convert(any(online.rabko.basketball.entity.User.class));
    }

    @Test
    void usersIdGet_shouldReturnUser() {
        Long id = 42L;
        online.rabko.basketball.entity.User entity = online.rabko.basketball.entity.User.builder()
            .id(id).username("john").build();
        when(userService.findById(id)).thenReturn(entity);
        when(converter.convert(entity)).thenReturn(new online.rabko.model.User());

        given()
            .when()
            .get("/users/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(userService).findById(id);
        verify(converter).convert(entity);
    }

    @Test
    void usersIdPut_shouldUpdateUser() {
        Long id = 7L;
        online.rabko.model.User incomingDto = new online.rabko.model.User();
        incomingDto.setId(999L);

        online.rabko.basketball.entity.User toUpdate = online.rabko.basketball.entity.User.builder()
            .username("new").build();
        when(converter.convertBack(any(online.rabko.model.User.class))).thenReturn(toUpdate);

        online.rabko.basketball.entity.User updated = online.rabko.basketball.entity.User.builder()
            .id(id).username("new").build();
        when(userService.update(eq(id), any(online.rabko.basketball.entity.User.class))).thenReturn(
            updated);
        when(converter.convert(updated)).thenReturn(new online.rabko.model.User());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/users/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(converter).convertBack(any(online.rabko.model.User.class));
        verify(userService).update(eq(id), any(online.rabko.basketball.entity.User.class));
        verify(converter).convert(updated);
    }

    @Test
    void usersIdDelete_shouldDeleteUser() {
        Long id = 9L;
        doNothing().when(userService).delete(id);

        given()
            .when()
            .delete("/users/{id}", id.toString())
            .then()
            .statusCode(204);

        verify(userService).delete(id);
        verifyNoInteractions(converter);
    }

    @Test
    void usersIdGet_shouldReturnNotFound() {
        Long id = 404L;
        when(userService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        given()
            .when()
            .get("/users/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void usersIdDelete_shouldReturnNotFound() {
        Long id = 404L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).delete(id);

        given()
            .when()
            .delete("/users/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void usersIdPut_shouldReturnBadRequest() {
        Long id = 5L;
        when(converter.convertBack(any(online.rabko.model.User.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/users/{id}", id.toString())
            .then()
            .statusCode(400);
    }
}
