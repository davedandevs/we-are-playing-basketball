package online.rabko.basketball.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.rabko.api.TeamsApi;
import online.rabko.basketball.controller.converter.TeamConverter;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * REST controller for managing teams.
 */
@Controller
@RequiredArgsConstructor
public class TeamsController implements TeamsApi {

    private final TeamService teamService;
    private final TeamConverter teamConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<online.rabko.model.Team>> teamsGet() {
        List<Team> teams = teamService.findAll();
        return ResponseEntity.ok(
            teams.stream()
                .map(teamConverter::convert)
                .toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Void> teamsIdDelete(Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<online.rabko.model.Team> teamsIdGet(Long id) {
        Team team = teamService.findById(id);
        return ResponseEntity.ok(teamConverter.convert(team));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<online.rabko.model.Team> teamsIdPut(Long id,
        online.rabko.model.Team teamDto) {
        Team team = teamConverter.convertBack(teamDto);
        Team updated = teamService.update(id, team);
        return ResponseEntity.ok(teamConverter.convert(updated));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<online.rabko.model.Team> teamsPost(online.rabko.model.Team teamDto) {
        Team team = teamConverter.convertBack(teamDto);
        Team created = teamService.create(team);
        return ResponseEntity.status(CREATED).body(teamConverter.convert(created));

    }
}
