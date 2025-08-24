package online.rabko.basketball.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.repository.TeamRepository;
import online.rabko.basketball.service.TeamService;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Team} entities.
 */
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Team findById(Long id) {
        return teamRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Team not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Team create(Team team) {
        if (teamRepository.existsByNameIgnoreCase(team.getName())) {
            throw new EntityExistsException("Team already exists: " + team.getName());
        }
        return teamRepository.save(team);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Team not found: " + id);
        }
        teamRepository.deleteById(id);
    }
}
