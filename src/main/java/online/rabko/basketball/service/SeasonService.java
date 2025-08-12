package online.rabko.basketball.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import online.rabko.basketball.entity.Season;

/**
 * Service interface for managing {@link Season} entities.
 */
public interface SeasonService {

    /**
     * Retrieves all seasons.
     *
     * @return list of all seasons.
     */
    List<Season> findAll();

    /**
     * Retrieves a season by ID.
     *
     * @param id the season ID.
     * @return the found season.
     * @throws EntityNotFoundException if no season with the given ID exists.
     */
    Season findById(Long id);

    /**
     * Creates a new season.
     *
     * @param season the season to create.
     * @return the created season.
     * @throws EntityExistsException    if a season with the same name exists or dates overlap.
     * @throws IllegalArgumentException if start date is after end date.
     */
    Season create(Season season);

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
    Season update(Long id, Season season);

    /**
     * Deletes a season by ID.
     *
     * @param id the season ID.
     * @throws EntityNotFoundException if the season does not exist.
     */
    void delete(Long id);
}
