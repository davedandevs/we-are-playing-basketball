package online.rabko.basketball.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.rabko.api.TeamsApi;
import online.rabko.basketball.controller.converter.TeamConverter;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.service.impl.TeamServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * REST controller for managing teams.
 */
@Controller
@RequiredArgsConstructor
public class TeamsController implements TeamsApi {

    private final TeamServiceImpl teamServiceImpl;
    private final TeamConverter teamConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<online.rabko.model.Team>> teamsGet() {
        List<Team> teams = teamServiceImpl.findAll();
        return ResponseEntity.ok(
            teams.stream()
                .map(teamConverter::convert)
                .toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> teamsIdDelete(Long id) {
        teamServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<online.rabko.model.Team> teamsIdGet(Long id) {
        Team team = teamServiceImpl.findById(id);
        return ResponseEntity.ok(teamConverter.convert(team));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<online.rabko.model.Team> teamsIdPut(Long id,
        online.rabko.model.Team teamDto) {
        Team team = teamConverter.convertBack(teamDto);
        Team updated = teamServiceImpl.update(id, team);
        return ResponseEntity.ok(teamConverter.convert(updated));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<online.rabko.model.Team> teamsPost(online.rabko.model.Team teamDto) {
        Team team = teamConverter.convertBack(teamDto);
        Team created = teamServiceImpl.create(team);
        return ResponseEntity.status(CREATED).body(teamConverter.convert(created));

    }
}
