package online.rabko.basketball.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.repository.TeamRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Team} entities.
 */
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    /**
     * Retrieves all teams from the database.
     *
     * @return list of all teams
     */
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    /**
     * Retrieves a team by its ID.
     *
     * @param id the team ID
     * @return the team with the given ID
     * @throws EntityNotFoundException if the team does not exist
     */
    public Team findById(Long id) {
        return teamRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Team not found: " + id));
    }

    /**
     * Creates a new team if it does not already exist.
     *
     * @param team the team entity to create
     * @return the created team
     * @throws EntityExistsException if a team with the same name already exists
     */
    public Team create(Team team) {
        if (teamRepository.existsByNameIgnoreCase(team.getName())) {
            throw new EntityExistsException("Team already exists: " + team.getName());
        }
        return teamRepository.save(team);
    }

    /**
     * Updates an existing team.
     *
     * @param id   the team ID
     * @param team the updated team entity
     * @return the updated team
     * @throws EntityNotFoundException if the team does not exist
     * @throws EntityExistsException   if another team with the same name already exists
     */
    public Team update(Long id, Team team) {
        Team existing = findById(id);
        if (teamRepository.existsByNameIgnoreCase(team.getName())
            && !Objects.equals(existing.getName(), team.getName())) {
            throw new EntityExistsException("Team already exists: " + team.getName());
        }
        team.setId(id);
        return teamRepository.save(team);
    }

    /**
     * Deletes a team by its ID.
     *
     * @param id the team ID
     * @throws EntityNotFoundException if the team does not exist
     */
    public void delete(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Team not found: " + id);
        }
        teamRepository.deleteById(id);
    }
}
