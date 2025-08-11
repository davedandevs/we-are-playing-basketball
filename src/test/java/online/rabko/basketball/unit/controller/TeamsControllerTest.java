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
import online.rabko.basketball.controller.TeamsController;
import online.rabko.basketball.controller.converter.TeamConverter;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Unit tests for {@link TeamsController}.
 */
@ExtendWith(MockitoExtension.class)
class TeamsControllerTest {

    @Mock
    private TeamService teamService;

    @Mock
    private TeamConverter teamConverter;

    @InjectMocks
    private TeamsController teamsController;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(teamsController);
    }

    @Test
    void teamsGet_shouldReturnList() {
        Team t1 = Team.builder().id(1L).name("Team A").build();
        Team t2 = Team.builder().id(2L).name("Team B").build();
        when(teamService.findAll()).thenReturn(List.of(t1, t2));
        when(teamConverter.convert(t1)).thenReturn(new online.rabko.model.Team());
        when(teamConverter.convert(t2)).thenReturn(new online.rabko.model.Team());

        given()
            .when()
            .get("/teams")
            .then()
            .statusCode(200)
            .body("$", hasSize(2));

        verify(teamService).findAll();
        verify(teamConverter, times(2)).convert(any(Team.class));
    }

    @Test
    void teamsIdGet_shouldReturnTeam() {
        Long id = 42L;
        Team entity = Team.builder().id(id).name("Team X").build();
        when(teamService.findById(id)).thenReturn(entity);
        when(teamConverter.convert(entity)).thenReturn(new online.rabko.model.Team());

        given()
            .when()
            .get("/teams/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(teamService).findById(id);
        verify(teamConverter).convert(entity);
    }

    @Test
    void teamsPost_shouldCreateTeam() {
        Team toCreate = Team.builder().name("New Team").build();
        Team created = Team.builder().id(10L).name("New Team").build();
        when(teamConverter.convertBack(any(online.rabko.model.Team.class))).thenReturn(toCreate);
        when(teamService.create(toCreate)).thenReturn(created);
        when(teamConverter.convert(created)).thenReturn(new online.rabko.model.Team());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/teams")
            .then()
            .statusCode(201)
            .body("$", notNullValue());

        verify(teamConverter).convertBack(any(online.rabko.model.Team.class));
        verify(teamService).create(toCreate);
        verify(teamConverter).convert(created);
    }

    @Test
    void teamsIdPut_shouldUpdateTeam() {
        Long id = 7L;
        Team toUpdate = Team.builder().name("Updated Team").build();
        Team updated = Team.builder().id(id).name("Updated Team").build();
        when(teamConverter.convertBack(any(online.rabko.model.Team.class))).thenReturn(toUpdate);
        when(teamService.update(eq(id), eq(toUpdate))).thenReturn(updated);
        when(teamConverter.convert(updated)).thenReturn(new online.rabko.model.Team());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/teams/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(teamConverter).convertBack(any(online.rabko.model.Team.class));
        verify(teamService).update(id, toUpdate);
        verify(teamConverter).convert(updated);
    }

    @Test
    void teamsIdDelete_shouldDeleteTeam() {
        Long id = 9L;
        doNothing().when(teamService).delete(id);

        given()
            .when()
            .delete("/teams/{id}", id.toString())
            .then()
            .statusCode(204);

        verify(teamService).delete(id);
        verifyNoInteractions(teamConverter);
    }

    @Test
    void teamsIdGet_shouldReturnNotFound() {
        Long id = 404L;
        when(teamService.findById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        given()
            .when()
            .get("/teams/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void teamsIdDelete_shouldReturnConflict() {
        Long id = 1L;
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
            .when(teamService).delete(id);

        given()
            .when()
            .delete("/teams/{id}", id.toString())
            .then()
            .statusCode(409);
    }
}
