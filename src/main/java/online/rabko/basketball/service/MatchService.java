package online.rabko.basketball.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import online.rabko.basketball.entity.Match;

/**
 * Service interface for managing {@link Match} entities.
 */
public interface MatchService {

    /**
     * Retrieves all matches.
     *
     * @return list of all matches.
     */
    List<Match> findAll();

    /**
     * Retrieves a match by ID.
     *
     * @param id the match ID.
     * @return the found match.
     * @throws EntityNotFoundException if no match with the given ID exists.
     */
    Match findById(Long id);

    /**
     * Creates a new match with validated season and teams.
     *
     * @param match the match to create (containing season/home/away IDs).
     * @return the created match.
     * @throws EntityNotFoundException  if season or teams are not found.
     * @throws IllegalArgumentException if teams are equal or date is outside the season dates.
     */
    Match create(Match match);

    /**
     * Updates an existing match.
     *
     * @param id    the match ID.
     * @param match the match data to update.
     * @return the updated match.
     * @throws EntityNotFoundException  if the match does not exist or season/teams are not found.
     * @throws IllegalArgumentException if teams are equal or date is outside the season dates.
     */
    Match update(Long id, Match match);

    /**
     * Deletes a match by ID.
     *
     * @param id the match ID.
     * @throws EntityNotFoundException if the match does not exist.
     */
    void delete(Long id);
}
