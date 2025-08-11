package online.rabko.basketball.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.rabko.api.MatchesApi;
import online.rabko.basketball.controller.converter.MatchConverter;
import online.rabko.basketball.entity.Match;
import online.rabko.basketball.service.MatchService;
import online.rabko.model.PlayerStats;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing matches.
 */
@RestController
@RequiredArgsConstructor
public class MatchesController implements MatchesApi {

    private final MatchService matchService;
    private final MatchConverter matchConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<online.rabko.model.Match>> matchesGet() {
        return ResponseEntity.ok(
            matchService.findAll().stream()
                .map(matchConverter::convert)
                .toList()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<online.rabko.model.Match> matchesIdGet(Long id) {
        return ResponseEntity.ok(
            matchConverter.convert(matchService.findById(id))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> matchesIdDelete(Long id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<online.rabko.model.Match> matchesPost(online.rabko.model.Match dto) {
        Match created = matchService.create(matchConverter.convertBack(dto));
        return ResponseEntity.status(CREATED).body(matchConverter.convert(created));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<online.rabko.model.Match> matchesIdPut(Long id,
        online.rabko.model.Match dto) {
        Match updated = matchService.update(id, matchConverter.convertBack(dto));
        return ResponseEntity.ok(matchConverter.convert(updated));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<PlayerStats>> matchesMatchIdStatsGet(Long matchId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<PlayerStats> matchesMatchIdStatsPost(Long matchId,
        PlayerStats playerStats) {
        return null;
    }
}
