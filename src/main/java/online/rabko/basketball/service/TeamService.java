package online.rabko.basketball.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import online.rabko.basketball.entity.Team;

/**
 * Service interface for managing {@link Team} entities.
 */
public interface TeamService {

    /**
     * Retrieves all teams from the database.
     *
     * @return list of all teams
     */
    List<Team> findAll();

    /**
     * Retrieves a team by its ID.
     *
     * @param id the team ID
     * @return the team with the given ID
     * @throws EntityNotFoundException if the team does not exist
     */
    Team findById(Long id);

    /**
     * Creates a new team if it does not already exist.
     *
     * @param team the team entity to create
     * @return the created team
     * @throws EntityExistsException if a team with the same name already exists
     */
    Team create(Team team);

    /**
     * Updates an existing team.
     *
     * @param id   the team ID
     * @param team the updated team entity
     * @return the updated team
     * @throws EntityNotFoundException if the team does not exist
     * @throws EntityExistsException   if another team with the same name already exists
     */
    Team update(Long id, Team team);

    /**
     * Deletes a team by its ID.
     *
     * @param id the team ID
     * @throws EntityNotFoundException if the team does not exist
     */
    void delete(Long id);
}
