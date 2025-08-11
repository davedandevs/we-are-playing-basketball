package online.rabko.basketball.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.Season;
import online.rabko.basketball.repository.SeasonRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Season} entities.
 */
@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonRepository seasonRepository;

    /**
     * Retrieves all seasons.
     *
     * @return list of all seasons.
     */
    public List<Season> findAll() {
        return seasonRepository.findAll();
    }

    /**
     * Retrieves a season by ID.
     *
     * @param id the season ID.
     * @return the found season.
     * @throws EntityNotFoundException if no season with the given ID exists.
     */
    public Season findById(Long id) {
        return seasonRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Season not found: " + id));
    }

    /**
     * Creates a new season.
     *
     * @param season the season to create.
     * @return the created season.
     * @throws EntityExistsException    if a season with the same name exists or dates overlap.
     * @throws IllegalArgumentException if start date is after end date.
     */
    public Season create(Season season) {
        validateDates(season.getStartDate(), season.getEndDate());
        if (seasonRepository.existsByNameIgnoreCase(season.getName())) {
            throw new EntityExistsException("Season already exists: " + season.getName());
        }
        if (seasonRepository.overlaps(season.getStartDate(), season.getEndDate(), null)) {
            throw new EntityExistsException("Season dates overlap with an existing season.");
        }
        return seasonRepository.save(season);
    }

    /**
     * Updates an existing season.
     *
     * @param id     the season ID.
     * @param season the season data to update.
     * @return the updated season.
     * @throws EntityNotFoundException  if the season does not exist.
     * @throws EntityExistsException    if another season with the same name exists or dates
     *                                  overlap.
     * @throws IllegalArgumentException if start date is after end date.
     */
    public Season update(Long id, Season season) {
        Season existing = findById(id);
        validateDates(season.getStartDate(), season.getEndDate());

        if (seasonRepository.existsByNameIgnoreCase(season.getName())
            && !Objects.equals(existing.getName(), season.getName())) {
            throw new EntityExistsException("Season already exists: " + season.getName());
        }
        if (seasonRepository.overlaps(season.getStartDate(), season.getEndDate(), id)) {
            throw new EntityExistsException("Season dates overlap with an existing season.");
        }

        season.setId(id);
        return seasonRepository.save(season);
    }

    /**
     * Deletes a season by ID.
     *
     * @param id the season ID.
     * @throws EntityNotFoundException if the season does not exist.
     */
    public void delete(Long id) {
        if (!seasonRepository.existsById(id)) {
            throw new EntityNotFoundException("Season not found: " + id);
        }
        seasonRepository.deleteById(id);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (Objects.isNull(start) || Objects.isNull(end)) {
            throw new IllegalArgumentException("Season startDate and endDate are required.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Season endDate must not be before startDate.");
        }
    }
}
