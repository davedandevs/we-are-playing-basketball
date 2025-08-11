package online.rabko.basketball.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import online.rabko.basketball.entity.Match;
import online.rabko.basketball.entity.Season;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.repository.MatchRepository;
import online.rabko.basketball.repository.SeasonRepository;
import online.rabko.basketball.repository.TeamRepository;
import online.rabko.basketball.service.MatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link MatchService}.
 */
@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    void findAll_shouldReturnList() {
        Match m1 = Match.builder().id(1L).build();
        Match m2 = Match.builder().id(2L).build();
        when(matchRepository.findAll()).thenReturn(List.of(m1, m2));

        List<Match> result = matchService.findAll();

        assertEquals(2, result.size());
        assertEquals(m1, result.get(0));
        assertEquals(m2, result.get(1));
        verify(matchRepository).findAll();
    }

    @Test
    void findById_shouldReturnMatch_whenExists() {
        Long id = 10L;
        Match existing = Match.builder().id(id).build();
        when(matchRepository.findById(id)).thenReturn(Optional.of(existing));

        Match result = matchService.findById(id);

        assertEquals(existing, result);
        verify(matchRepository).findById(id);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        Long id = 404L;
        when(matchRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> matchService.findById(id));
        assertEquals("Match not found: " + id, ex.getMessage());
    }

    @Test
    void create_shouldSave_whenValid_andDefaultScoresIfNull() {
        Long seasonId = 1L;
        Long homeId = 10L;
        Long awayId = 20L;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        LocalDate date = LocalDate.of(2025, 6, 15);

        Season season = Season.builder().id(seasonId).startDate(start).endDate(end).build();
        Team home = Team.builder().id(homeId).build();
        Team away = Team.builder().id(awayId).build();

        Match toCreate = Match.builder()
            .season(Season.builder().id(seasonId).build())
            .homeTeam(Team.builder().id(homeId).build())
            .awayTeam(Team.builder().id(awayId).build())
            .date(date)
            .homeTeamScore(null)
            .awayTeamScore(null)
            .build();

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(teamRepository.findById(homeId)).thenReturn(Optional.of(home));
        when(teamRepository.findById(awayId)).thenReturn(Optional.of(away));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        Match result = matchService.create(toCreate);

        assertNotNull(result);
        assertEquals(0, result.getHomeTeamScore());
        assertEquals(0, result.getAwayTeamScore());
        assertEquals(seasonId, result.getSeason().getId());
        assertEquals(homeId, result.getHomeTeam().getId());
        assertEquals(awayId, result.getAwayTeam().getId());

        verify(seasonRepository).findById(seasonId);
        verify(teamRepository).findById(homeId);
        verify(teamRepository).findById(awayId);

        ArgumentCaptor<Match> captor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository).save(captor.capture());
        Match saved = captor.getValue();
        assertEquals(date, saved.getDate());
        assertEquals(0, saved.getHomeTeamScore());
        assertEquals(0, saved.getAwayTeamScore());
    }

    @Test
    void create_shouldThrow_whenRelationsMissing() {
        Match bad = Match.builder().build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> matchService.create(bad));
        assertEquals("Season, homeTeam and awayTeam are required.", ex.getMessage());

        verifyNoInteractions(seasonRepository, teamRepository, matchRepository);
    }

    @Test
    void create_shouldThrow_whenSeasonNotFound() {
        Long seasonId = 1L;
        Match m = Match.builder()
            .season(Season.builder().id(seasonId).build())
            .homeTeam(Team.builder().id(10L).build())
            .awayTeam(Team.builder().id(20L).build())
            .date(LocalDate.of(2025, 6, 1))
            .build();

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> matchService.create(m));
        assertEquals("Season not found: " + seasonId, ex.getMessage());
    }

    @Test
    void create_shouldThrow_whenHomeTeamNotFound() {
        Long seasonId = 1L;
        Long homeId = 10L;
        Season season = Season.builder().id(seasonId)
            .startDate(LocalDate.of(2025, 1, 1))
            .endDate(LocalDate.of(2025, 12, 31))
            .build();

        Match m = Match.builder()
            .season(Season.builder().id(seasonId).build())
            .homeTeam(Team.builder().id(homeId).build())
            .awayTeam(Team.builder().id(20L).build())
            .date(LocalDate.of(2025, 6, 1))
            .build();

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(teamRepository.findById(homeId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> matchService.create(m));
        assertEquals("Team not found: " + homeId, ex.getMessage());
    }

    @Test
    void create_shouldThrow_whenAwayTeamNotFound() {
        Long seasonId = 1L;
        Long awayId = 20L;
        Season season = Season.builder().id(seasonId)
            .startDate(LocalDate.of(2025, 1, 1))
            .endDate(LocalDate.of(2025, 12, 31))
            .build();

        Match m = Match.builder()
            .season(Season.builder().id(seasonId).build())
            .homeTeam(Team.builder().id(10L).build())
            .awayTeam(Team.builder().id(awayId).build())
            .date(LocalDate.of(2025, 6, 1))
            .build();

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(Team.builder().id(10L).build()));
        when(teamRepository.findById(awayId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> matchService.create(m));
        assertEquals("Team not found: " + awayId, ex.getMessage());
    }

    @Test
    void create_shouldThrow_whenSameTeams() {
        Long seasonId = 1L;
        Long sameId = 10L;
        Season season = Season.builder().id(seasonId)
            .startDate(LocalDate.of(2025, 1, 1))
            .endDate(LocalDate.of(2025, 12, 31))
            .build();

        Match m = Match.builder()
            .season(Season.builder().id(seasonId).build())
            .homeTeam(Team.builder().id(sameId).build())
            .awayTeam(Team.builder().id(sameId).build())
            .date(LocalDate.of(2025, 6, 1))
            .build();

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(teamRepository.findById(sameId)).thenReturn(
            Optional.of(Team.builder().id(sameId).build()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> matchService.create(m));
        assertEquals("Home and away teams must be different.", ex.getMessage());
    }

    @Test
    void create_shouldThrow_whenDateNull() {
        Long seasonId = 1L;
        Season season = Season.builder().id(seasonId)
            .startDate(LocalDate.of(2025, 1, 1))
            .endDate(LocalDate.of(2025, 12, 31))
            .build();

        Match m = Match.builder()
            .season(Season.builder().id(seasonId).build())
            .homeTeam(Team.builder().id(10L).build())
            .awayTeam(Team.builder().id(20L).build())
            .build();

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(Team.builder().id(10L).build()));
        when(teamRepository.findById(20L)).thenReturn(Optional.of(Team.builder().id(20L).build()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> matchService.create(m));
        assertEquals("Match date is required.", ex.getMessage());
    }

    @Test
    void create_shouldThrow_whenDateOutsideSeason() {
        Long seasonId = 1L;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        LocalDate date = LocalDate.of(2026, 1, 1);

        Season season = Season.builder().id(seasonId).startDate(start).endDate(end).build();

        Match m = Match.builder()
            .season(Season.builder().id(seasonId).build())
            .homeTeam(Team.builder().id(10L).build())
            .awayTeam(Team.builder().id(20L).build())
            .date(date)
            .build();

        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(Team.builder().id(10L).build()));
        when(teamRepository.findById(20L)).thenReturn(Optional.of(Team.builder().id(20L).build()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> matchService.create(m));
        assertEquals("Match date must be within the season dates.", ex.getMessage());
    }

    @Test
    void update_shouldSave_whenValid() {
        Long id = 5L;
        Long seasonId = 1L;
        Long homeId = 10L;
        Long awayId = 20L;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        LocalDate date = LocalDate.of(2025, 7, 7);

        Season season = Season.builder().id(seasonId).startDate(start).endDate(end).build();
        Team home = Team.builder().id(homeId).build();
        Team away = Team.builder().id(awayId).build();

        Match replacement = Match.builder()
            .season(Season.builder().id(seasonId).build())
            .homeTeam(Team.builder().id(homeId).build())
            .awayTeam(Team.builder().id(awayId).build())
            .date(date)
            .homeTeamScore(55)
            .awayTeamScore(60)
            .build();

        when(matchRepository.findById(id)).thenReturn(Optional.of(Match.builder().id(id).build()));
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(teamRepository.findById(homeId)).thenReturn(Optional.of(home));
        when(teamRepository.findById(awayId)).thenReturn(Optional.of(away));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        Match updated = matchService.update(id, replacement);

        assertEquals(id, updated.getId());
        assertEquals(55, updated.getHomeTeamScore());
        assertEquals(60, updated.getAwayTeamScore());
        verify(matchRepository).findById(id);
        verify(matchRepository).save(argThat(m ->
            m.getId().equals(id) && m.getSeason().getId().equals(seasonId)
                && m.getHomeTeam().getId().equals(homeId) && m.getAwayTeam().getId().equals(awayId)
                && m.getDate().equals(date)
        ));
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        Long id = 404L;
        Match replacement = Match.builder().season(Season.builder().id(1L).build())
            .homeTeam(Team.builder().id(10L).build())
            .awayTeam(Team.builder().id(20L).build())
            .date(LocalDate.of(2025, 6, 1))
            .build();

        when(matchRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> matchService.update(id, replacement));
        assertEquals("Match not found: " + id, ex.getMessage());
        verify(matchRepository, never()).save(any());
    }

    @Test
    void delete_shouldDelete_whenExists() {
        Long id = 9L;
        when(matchRepository.existsById(id)).thenReturn(true);

        matchService.delete(id);

        verify(matchRepository).existsById(id);
        verify(matchRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        Long id = 404L;
        when(matchRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> matchService.delete(id));
        assertEquals("Match not found: " + id, ex.getMessage());
        verify(matchRepository, never()).deleteById(any());
    }
}
