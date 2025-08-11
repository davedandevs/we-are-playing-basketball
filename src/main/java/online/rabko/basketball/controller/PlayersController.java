package online.rabko.basketball.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.rabko.api.PlayersApi;
import online.rabko.basketball.controller.converter.PlayerConverter;
import online.rabko.basketball.service.PlayerService;
import online.rabko.basketball.service.TeamService;
import online.rabko.model.Player;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for {@code /players} endpoints. Implements {@link PlayersApi}.
 */
@RestController
@RequiredArgsConstructor
public class PlayersController implements PlayersApi {

    private final PlayerService playerService;
    private final TeamService teamService;
    private final PlayerConverter playerConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<Player>> playersGet() {
        List<Player> body = playerService.findAll()
            .stream()
            .map(playerConverter::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Player> playersIdGet(Long id) {
        online.rabko.basketball.entity.Player entity = playerService.findById(id);
        return ResponseEntity.ok(playerConverter.convert(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Player> playersPost(Player dto) {
        dto.setId(null);
        online.rabko.basketball.entity.Player entity = playerConverter.convertBack(dto);
        if (Objects.nonNull(dto.getTeamId())) {
            entity.setTeam(teamService.findById(dto.getTeamId()));
        }
        online.rabko.basketball.entity.Player created = playerService.create(entity);
        return ResponseEntity.status(CREATED).body(playerConverter.convert(created));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Player> playersIdPut(Long id, Player dto) {
        dto.setId(null);
        online.rabko.basketball.entity.Player entity = playerConverter.convertBack(dto);
        entity.setId(id);
        if (Objects.nonNull(dto.getTeamId())) {
            entity.setTeam(teamService.findById(dto.getTeamId()));
        } else {
            entity.setTeam(null);
        }
        online.rabko.basketball.entity.Player updated = playerService.update(id, entity);
        return ResponseEntity.ok(playerConverter.convert(updated));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> playersIdDelete(Long id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
