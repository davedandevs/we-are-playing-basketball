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
import online.rabko.basketball.controller.SeasonsController;
import online.rabko.basketball.controller.converter.SeasonConverter;
import online.rabko.basketball.entity.Season;
import online.rabko.basketball.service.impl.SeasonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Unit tests for {@link SeasonsController}.
 */
@ExtendWith(MockitoExtension.class)
class SeasonsControllerTest {

    @Mock
    private SeasonServiceImpl seasonServiceImpl;

    @Mock
    private SeasonConverter seasonConverter;

    @InjectMocks
    private SeasonsController seasonsController;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(seasonsController);
    }

    @Test
    void seasonsGet_shouldReturnList() {
        Season s1 = Season.builder().id(1L).build();
        Season s2 = Season.builder().id(2L).build();
        when(seasonServiceImpl.findAll()).thenReturn(List.of(s1, s2));
        when(seasonConverter.convert(s1)).thenReturn(new online.rabko.model.Season());
        when(seasonConverter.convert(s2)).thenReturn(new online.rabko.model.Season());

        given()
            .when()
            .get("/seasons")
            .then()
            .statusCode(200)
            .body("$", hasSize(2));

        verify(seasonServiceImpl).findAll();
        verify(seasonConverter, times(2)).convert(any(Season.class));
    }

    @Test
    void seasonsIdGet_shouldReturnSeason() {
        Long id = 42L;
        Season entity = Season.builder().id(id).build();
        when(seasonServiceImpl.findById(id)).thenReturn(entity);
        when(seasonConverter.convert(entity)).thenReturn(new online.rabko.model.Season());

        given()
            .when()
            .get("/seasons/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(seasonServiceImpl).findById(id);
        verify(seasonConverter).convert(entity);
    }

    @Test
    void seasonsPost_shouldCreateSeason() {
        Season toCreate = Season.builder().build();
        Season created = Season.builder().id(10L).build();
        when(seasonConverter.convertBack(any(online.rabko.model.Season.class))).thenReturn(
            toCreate);
        when(seasonServiceImpl.create(toCreate)).thenReturn(created);
        when(seasonConverter.convert(created)).thenReturn(new online.rabko.model.Season());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/seasons")
            .then()
            .statusCode(201)
            .body("$", notNullValue());

        verify(seasonConverter).convertBack(any(online.rabko.model.Season.class));
        verify(seasonServiceImpl).create(toCreate);
        verify(seasonConverter).convert(created);
    }

    @Test
    void seasonsIdPut_shouldUpdateSeason() {
        Long id = 7L;
        Season replacement = Season.builder().build();
        Season updated = Season.builder().id(id).build();
        when(seasonConverter.convertBack(any(online.rabko.model.Season.class))).thenReturn(
            replacement);
        when(seasonServiceImpl.update(eq(id), eq(replacement))).thenReturn(updated);
        when(seasonConverter.convert(updated)).thenReturn(new online.rabko.model.Season());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/seasons/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(seasonConverter).convertBack(any(online.rabko.model.Season.class));
        verify(seasonServiceImpl).update(id, replacement);
        verify(seasonConverter).convert(updated);
    }

    @Test
    void seasonsIdDelete_shouldDeleteSeason() {
        Long id = 9L;
        doNothing().when(seasonServiceImpl).delete(id);

        given()
            .when()
            .delete("/seasons/{id}", id.toString())
            .then()
            .statusCode(204);

        verify(seasonServiceImpl).delete(id);
        verifyNoInteractions(seasonConverter);
    }

    @Test
    void seasonsIdGet_shouldReturnNotFound() {
        Long id = 404L;
        when(seasonServiceImpl.findById(id)).thenThrow(
            new ResponseStatusException(HttpStatus.NOT_FOUND));

        given()
            .when()
            .get("/seasons/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void seasonsIdDelete_shouldReturnNotFound() {
        Long id = 404L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(seasonServiceImpl)
            .delete(id);

        given()
            .when()
            .delete("/seasons/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void seasonsPost_shouldReturnBadRequest() {
        when(seasonConverter.convertBack(any(online.rabko.model.Season.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/seasons")
            .then()
            .statusCode(400);
    }

    @Test
    void seasonsIdPut_shouldReturnBadRequest() {
        Long id = 5L;
        when(seasonConverter.convertBack(any(online.rabko.model.Season.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/seasons/{id}", id.toString())
            .then()
            .statusCode(400);
    }
}
