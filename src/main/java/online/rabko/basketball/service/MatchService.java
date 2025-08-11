package online.rabko.basketball.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.Match;
import online.rabko.basketball.entity.Season;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.repository.MatchRepository;
import online.rabko.basketball.repository.SeasonRepository;
import online.rabko.basketball.repository.TeamRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Match} entities.
 */
@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;
    private final TeamRepository teamRepository;

    /**
     * Retrieves all matches.
     *
     * @return list of all matches.
     */
    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    /**
     * Retrieves a match by ID.
     *
     * @param id the match ID.
     * @return the found match.
     * @throws EntityNotFoundException if no match with the given ID exists.
     */
    public Match findById(Long id) {
        return matchRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Match not found: " + id));
    }

    /**
     * Creates a new match with validated season and teams.
     *
     * @param match the match to create (containing season/home/away IDs).
     * @return the created match.
     * @throws EntityNotFoundException  if season or teams are not found.
     * @throws IllegalArgumentException if teams are equal or date is outside the season dates.
     */
    public Match create(Match match) {
        resolveAndValidateRelations(match);
        defaultScoresIfNull(match);
        return matchRepository.save(match);
    }

    /**
     * Updates an existing match.
     *
     * @param id    the match ID.
     * @param match the match data to update.
     * @return the updated match.
     * @throws EntityNotFoundException  if the match does not exist or season/teams are not found.
     * @throws IllegalArgumentException if teams are equal or date is outside the season dates.
     */
    public Match update(Long id, Match match) {
        findById(id);
        match.setId(id);
        resolveAndValidateRelations(match);
        defaultScoresIfNull(match);
        return matchRepository.save(match);
    }

    /**
     * Deletes a match by ID.
     *
     * @param id the match ID.
     * @throws EntityNotFoundException if the match does not exist.
     */
    public void delete(Long id) {
        if (!matchRepository.existsById(id)) {
            throw new EntityNotFoundException("Match not found: " + id);
        }
        matchRepository.deleteById(id);
    }

    private void resolveAndValidateRelations(Match match) {
        Long seasonId = Objects.nonNull(match.getSeason()) ? match.getSeason().getId() : null;
        Long homeId = Objects.nonNull(match.getHomeTeam()) ? match.getHomeTeam().getId() : null;
        Long awayId = Objects.nonNull(match.getAwayTeam()) ? match.getAwayTeam().getId() : null;

        if (Objects.isNull(seasonId) || Objects.isNull(homeId) || Objects.isNull(awayId)) {
            throw new IllegalArgumentException("Season, homeTeam and awayTeam are required.");
        }

        Season season = seasonRepository.findById(seasonId)
            .orElseThrow(() -> new EntityNotFoundException("Season not found: " + seasonId));
        Team home = teamRepository.findById(homeId)
            .orElseThrow(() -> new EntityNotFoundException("Team not found: " + homeId));
        Team away = teamRepository.findById(awayId)
            .orElseThrow(() -> new EntityNotFoundException("Team not found: " + awayId));

        if (Objects.equals(home.getId(), away.getId())) {
            throw new IllegalArgumentException("Home and away teams must be different.");
        }
        if (Objects.isNull(match.getDate())) {
            throw new IllegalArgumentException("Match date is required.");
        }
        if (match.getDate().isBefore(season.getStartDate()) || match.getDate()
            .isAfter(season.getEndDate())) {
            throw new IllegalArgumentException("Match date must be within the season dates.");
        }
        match.setSeason(season);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
    }

    private void defaultScoresIfNull(Match match) {
        if (Objects.isNull(match.getHomeTeamScore())) {
            match.setHomeTeamScore(0);
        }
        if (Objects.isNull(match.getAwayTeamScore())) {
            match.setAwayTeamScore(0);
        }
    }
}
