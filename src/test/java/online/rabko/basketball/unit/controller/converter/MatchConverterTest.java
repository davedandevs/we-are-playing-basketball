package online.rabko.basketball.unit.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import online.rabko.basketball.controller.converter.MatchConverter;
import online.rabko.basketball.entity.Match;
import online.rabko.basketball.entity.Season;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.service.SeasonService;
import online.rabko.basketball.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link MatchConverter}.
 */
@ExtendWith(MockitoExtension.class)
class MatchConverterTest {

    @Mock
    private SeasonService seasonService;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private MatchConverter converter;

    private Season season;
    private Team home;
    private Team away;

    @BeforeEach
    void setUp() {
        season = Season.builder().id(1L).build();
        home = Team.builder().id(10L).build();
        away = Team.builder().id(20L).build();
    }

    @Test
    void convert_shouldMapAllFields() {
        LocalDate date = LocalDate.of(2025, 1, 2);
        Match src = Match.builder()
            .id(100L)
            .season(season)
            .date(date)
            .homeTeam(home)
            .awayTeam(away)
            .homeTeamScore(88)
            .awayTeamScore(77)
            .build();

        online.rabko.model.Match dto = converter.convert(src);

        assertEquals(100L, dto.getId());
        assertEquals(1L, dto.getSeasonId());
        assertEquals(date, dto.getDate());
        assertEquals(10L, dto.getHomeTeamId());
        assertEquals(20L, dto.getAwayTeamId());
        assertEquals(88, dto.getHomeTeamScore());
        assertEquals(77, dto.getAwayTeamScore());
    }

    @Test
    void convert_shouldHandleNullRelations() {
        LocalDate date = LocalDate.of(2025, 2, 3);
        Match src = Match.builder()
            .id(101L)
            .season(null)
            .date(date)
            .homeTeam(null)
            .awayTeam(null)
            .homeTeamScore(50)
            .awayTeamScore(60)
            .build();

        online.rabko.model.Match dto = converter.convert(src);

        assertEquals(101L, dto.getId());
        assertEquals(date, dto.getDate());
        assertNull(dto.getSeasonId());
        assertNull(dto.getHomeTeamId());
        assertNull(dto.getAwayTeamId());
        assertEquals(50, dto.getHomeTeamScore());
        assertEquals(60, dto.getAwayTeamScore());
    }

    @Test
    void convertBack_shouldMapAllFields_andLookupRelations() {
        LocalDate date = LocalDate.of(2025, 3, 4);
        online.rabko.model.Match dto = new online.rabko.model.Match()
            .id(999L)
            .seasonId(1L)
            .date(date)
            .homeTeamId(10L)
            .awayTeamId(20L)
            .homeTeamScore(70)
            .awayTeamScore(71);

        when(seasonService.findById(1L)).thenReturn(season);
        when(teamService.findById(10L)).thenReturn(home);
        when(teamService.findById(20L)).thenReturn(away);

        Match entity = converter.convertBack(dto);

        assertNull(entity.getId());
        assertEquals(date, entity.getDate());
        assertEquals(70, entity.getHomeTeamScore());
        assertEquals(71, entity.getAwayTeamScore());
        assertNotNull(entity.getSeason());
        assertEquals(1L, entity.getSeason().getId());
        assertNotNull(entity.getHomeTeam());
        assertEquals(10L, entity.getHomeTeam().getId());
        assertNotNull(entity.getAwayTeam());
        assertEquals(20L, entity.getAwayTeam().getId());

        verify(seasonService).findById(eq(1L));
        verify(teamService).findById(eq(10L));
        verify(teamService).findById(eq(20L));
    }

    @Test
    void convertBack_shouldSkipLookups_whenIdsNull() {
        LocalDate date = LocalDate.of(2025, 4, 5);
        online.rabko.model.Match dto = new online.rabko.model.Match()
            .date(date)
            .homeTeamScore(10)
            .awayTeamScore(11);

        Match entity = converter.convertBack(dto);

        assertEquals(date, entity.getDate());
        assertEquals(10, entity.getHomeTeamScore());
        assertEquals(11, entity.getAwayTeamScore());
        assertNull(entity.getSeason());
        assertNull(entity.getHomeTeam());
        assertNull(entity.getAwayTeam());

        verifyNoInteractions(seasonService);
        verifyNoInteractions(teamService);
    }
}
