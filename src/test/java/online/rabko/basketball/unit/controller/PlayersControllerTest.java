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
import online.rabko.basketball.controller.PlayersController;
import online.rabko.basketball.controller.converter.PlayerConverter;
import online.rabko.basketball.entity.Player;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.service.PlayerService;
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
 * Unit tests for {@link PlayersController}.
 */
@ExtendWith(MockitoExtension.class)
class PlayersControllerTest {

    @Mock
    private PlayerService playerService;

    @Mock
    private TeamService teamService;

    @Mock
    private PlayerConverter playerConverter;

    @InjectMocks
    private PlayersController playersController;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.standaloneSetup(playersController);
    }

    @Test
    void playersGet_shouldReturnList() {
        Player p1 = Player.builder().id(1L).build();
        Player p2 = Player.builder().id(2L).build();
        when(playerService.findAll()).thenReturn(List.of(p1, p2));
        when(playerConverter.convert(p1)).thenReturn(new online.rabko.model.Player());
        when(playerConverter.convert(p2)).thenReturn(new online.rabko.model.Player());

        given()
            .when()
            .get("/players")
            .then()
            .statusCode(200)
            .body("$", hasSize(2));

        verify(playerService).findAll();
        verify(playerConverter, times(2)).convert(any(Player.class));
    }

    @Test
    void playersIdGet_shouldReturnPlayer() {
        Long id = 42L;
        Player entity = Player.builder().id(id).build();
        when(playerService.findById(id)).thenReturn(entity);
        when(playerConverter.convert(entity)).thenReturn(new online.rabko.model.Player());

        given()
            .when()
            .get("/players/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(playerService).findById(id);
        verify(playerConverter).convert(entity);
    }

    @Test
    void playersPost_shouldCreatePlayer_withoutTeam() {
        Player toCreate = Player.builder().build();
        Player created = Player.builder().id(10L).build();
        when(playerConverter.convertBack(any(online.rabko.model.Player.class))).thenReturn(
            toCreate);
        when(playerService.create(toCreate)).thenReturn(created);
        when(playerConverter.convert(created)).thenReturn(new online.rabko.model.Player());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/players")
            .then()
            .statusCode(201)
            .body("$", notNullValue());

        verifyNoInteractions(teamService);
        verify(playerConverter).convertBack(any(online.rabko.model.Player.class));
        verify(playerService).create(toCreate);
        verify(playerConverter).convert(created);
    }

    @Test
    void playersPost_shouldCreatePlayer_withTeam() throws Exception {
        Long teamId = 5L;
        Team team = Team.builder().id(teamId).build();
        Player toCreate = Player.builder().build();
        Player created = Player.builder().id(11L).team(team).build();

        when(playerConverter.convertBack(any(online.rabko.model.Player.class))).thenReturn(
            toCreate);
        when(teamService.findById(teamId)).thenReturn(team);
        when(playerService.create(any(Player.class))).thenReturn(created);
        when(playerConverter.convert(created)).thenReturn(new online.rabko.model.Player());

        online.rabko.model.Player dto = new online.rabko.model.Player();
        dto.setTeamId(teamId);

        given()
            .contentType(ContentType.JSON)
            .body(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto))
            .when()
            .post("/players")
            .then()
            .statusCode(201)
            .body("$", notNullValue());

        verify(teamService).findById(eq(teamId));
        verify(playerService).create(any(Player.class));
        verify(playerConverter).convert(created);
    }


    @Test
    void playersIdPut_shouldUpdatePlayer_withTeam() throws Exception {
        Long id = 7L;
        Long teamId = 3L;

        Team team = Team.builder().id(teamId).build();
        Player replacement = Player.builder().build();
        Player updated = Player.builder().id(id).team(team).build();

        when(playerConverter.convertBack(any(online.rabko.model.Player.class))).thenReturn(
            replacement);
        when(teamService.findById(teamId)).thenReturn(team);
        when(playerService.update(eq(id), any(Player.class))).thenReturn(updated);
        when(playerConverter.convert(updated)).thenReturn(new online.rabko.model.Player());
        online.rabko.model.Player dto = new online.rabko.model.Player();
        dto.setTeamId(teamId);
        given()
            .contentType(ContentType.JSON)
            .body(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(dto))
            .when()
            .put("/players/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verify(teamService).findById(eq(teamId));
        verify(playerService).update(eq(id), any(Player.class));
        verify(playerConverter).convert(updated);
    }


    @Test
    void playersIdPut_shouldUpdatePlayer_withoutTeam() {
        Long id = 8L;
        Player replacement = Player.builder().build();
        Player updated = Player.builder().id(id).team(null).build();

        when(playerConverter.convertBack(any(online.rabko.model.Player.class))).thenReturn(
            replacement);
        when(playerService.update(eq(id), any(Player.class))).thenReturn(updated);
        when(playerConverter.convert(updated)).thenReturn(new online.rabko.model.Player());

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/players/{id}", id.toString())
            .then()
            .statusCode(200)
            .body("$", notNullValue());

        verifyNoInteractions(teamService);
        verify(playerService).update(eq(id), any(Player.class));
        verify(playerConverter).convert(updated);
    }

    @Test
    void playersIdDelete_shouldDeletePlayer() {
        Long id = 9L;
        doNothing().when(playerService).delete(id);

        given()
            .when()
            .delete("/players/{id}", id.toString())
            .then()
            .statusCode(204);

        verify(playerService).delete(id);
        verifyNoInteractions(playerConverter, teamService);
    }

    @Test
    void playersIdGet_shouldReturnNotFound() {
        Long id = 404L;
        when(playerService.findById(id)).thenThrow(
            new ResponseStatusException(HttpStatus.NOT_FOUND));

        given()
            .when()
            .get("/players/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void playersIdDelete_shouldReturnNotFound() {
        Long id = 404L;
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(playerService).delete(id);

        given()
            .when()
            .delete("/players/{id}", id.toString())
            .then()
            .statusCode(404);
    }

    @Test
    void playersPost_shouldReturnBadRequest() {
        when(playerConverter.convertBack(any(online.rabko.model.Player.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .post("/players")
            .then()
            .statusCode(400);
    }

    @Test
    void playersIdPut_shouldReturnBadRequest() {
        Long id = 5L;
        when(playerConverter.convertBack(any(online.rabko.model.Player.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when()
            .put("/players/{id}", id.toString())
            .then()
            .statusCode(400);
    }
}
