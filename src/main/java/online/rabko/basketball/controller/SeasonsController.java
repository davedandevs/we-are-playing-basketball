package online.rabko.basketball.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.rabko.api.SeasonsApi;
import online.rabko.basketball.controller.converter.SeasonConverter;
import online.rabko.basketball.entity.Season;
import online.rabko.basketball.service.impl.SeasonServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing seasons.
 */
@RestController
@RequiredArgsConstructor
public class SeasonsController implements SeasonsApi {

    private final SeasonServiceImpl seasonServiceImpl;
    private final SeasonConverter seasonConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<online.rabko.model.Season>> seasonsGet() {
        List<Season> seasons = seasonServiceImpl.findAll();
        return ResponseEntity.ok(
            seasons.stream()
                .map(seasonConverter::convert)
                .toList()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<online.rabko.model.Season> seasonsIdGet(Long id) {
        Season season = seasonServiceImpl.findById(id);
        return ResponseEntity.ok(seasonConverter.convert(season));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> seasonsIdDelete(Long id) {
        seasonServiceImpl.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<online.rabko.model.Season> seasonsIdPut(
        Long id,
        online.rabko.model.Season seasonDto
    ) {
        seasonDto.setId(null);
        Season replacement = seasonConverter.convertBack(seasonDto);
        replacement.setId(id);
        Season updated = seasonServiceImpl.update(id, replacement);
        return ResponseEntity.ok(seasonConverter.convert(updated));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<online.rabko.model.Season> seasonsPost(
        online.rabko.model.Season seasonDto) {
        seasonDto.setId(null);
        Season toCreate = seasonConverter.convertBack(seasonDto);
        Season created = seasonServiceImpl.create(toCreate);
        return ResponseEntity.status(CREATED).body(seasonConverter.convert(created));
    }
}
