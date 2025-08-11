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
import online.rabko.basketball.controller.MatchesController;
import online.rabko.basketball.controller.converter.MatchConverter;
import online.rabko.basketball.entity.Match;
import online.rabko.basketball.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Unit tests for {@link MatchesController}.
 */
@ExtendWith(MockitoExtension.class)
class MatchesControllerTest {

    @Mock
    private MatchService matchService;

    @Mock
    private MatchConverter matchConverter;

    @InjectMocks
    private MatchesController matchesController;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(matchesController);
    }

    @Test
    void matchesGet_shouldReturnList() {
        Match m1 = Match.builder().id(1L).build();
        Match m2 = Match.builder().id(2L).build();
        when(matchService.findAll()).thenReturn(List.of(m1, m2));
        when(matchConverter.convert(m1)).thenReturn(new online.rabko.model.Match());
        when(matchConverter.convert(m2)).thenReturn(new online.rabko.model.Match());

        given()
            .when()
            .get("/matches")
            .then()
            .statusCode(200)
            .body("$", hasSize(2));

        verify(matchService).findAll();
        verify(matchConverter, times(2)).convert(any(Match.class));
    }

    @Test
    void matchesIdGet_shouldReturnMatch() {
        Long id = 42L;
        Match entity = Match.builder().id(id).build();
        when(matchService.findById(id)).thenReturn(entity);
        when(matchConverter.convert(entity)).thenReturn(new online.rabko.model.Match());

        given()
            .when()
            .get("/matches/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(matchService).findById(id);
        verify(matchConverter).convert(entity);
    }

    @Test
    void matchesPost_shouldCreateMatch() {
        Match toCreate = Match.builder().build();
        Match created = Match.builder().id(10L).build();
        when(matchConverter.convertBack(any(online.rabko.model.Match.class))).thenReturn(toCreate);
        when(matchService.create(toCreate)).thenReturn(created);
        when(matchConverter.convert(created)).thenReturn(new online.rabko.model.Match());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/matches")
            .then()
            .statusCode(201)
            .body("$", notNullValue());

        verify(matchConverter).convertBack(any(online.rabko.model.Match.class));
        verify(matchService).create(toCreate);
        verify(matchConverter).convert(created);
    }

    @Test
    void matchesIdPut_shouldUpdateMatch() {
        Long id = 7L;
        Match toUpdate = Match.builder().build();
        Match updated = Match.builder().id(id).build();
        when(matchConverter.convertBack(any(online.rabko.model.Match.class))).thenReturn(toUpdate);
        when(matchService.update(eq(id), eq(toUpdate))).thenReturn(updated);
        when(matchConverter.convert(updated)).thenReturn(new online.rabko.model.Match());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/matches/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(matchConverter).convertBack(any(online.rabko.model.Match.class));
        verify(matchService).update(id, toUpdate);
        verify(matchConverter).convert(updated);
    }

    @Test
    void matchesIdDelete_shouldDeleteMatch() {
        Long id = 9L;
        doNothing().when(matchService).delete(id);

        given()
            .when()
            .delete("/matches/{id}", id.toString())
            .then()
            .statusCode(204);

        verify(matchService).delete(id);
        verifyNoInteractions(matchConverter);
    }

    @Test
    void matchesIdGet_shouldReturnNotFound() {
        Long id = 404L;
        when(matchService.findById(id)).thenThrow(
            new ResponseStatusException(HttpStatus.NOT_FOUND));

        given()
            .when()
            .get("/matches/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void matchesIdDelete_shouldReturnNotFound() {
        Long id = 404L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
            .when(matchService).delete(id);

        given()
            .when()
            .delete("/matches/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void matchesPost_shouldReturnConflict() {
        when(matchConverter.convertBack(any(online.rabko.model.Match.class))).thenReturn(
            Match.builder().build());
        when(matchService.create(any(Match.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/matches")
            .then()
            .statusCode(409);
    }

    @Test
    void matchesIdPut_shouldReturnBadRequest() {
        Long id = 5L;
        when(matchConverter.convertBack(any(online.rabko.model.Match.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/matches/{id}", id.toString())
            .then()
            .statusCode(400);
    }

    @Test
    void matchesIdPut_shouldReturnConflict() {
        Long id = 6L;
        when(matchConverter.convertBack(any(online.rabko.model.Match.class))).thenReturn(
            Match.builder().build());
        when(matchService.update(eq(id), any(Match.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/matches/{id}", id.toString())
            .then()
            .statusCode(409);
    }
}
