package online.rabko.basketball.controller.converter;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.Match;
import online.rabko.basketball.service.SeasonService;
import online.rabko.basketball.service.TeamService;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Match} entity and {@link online.rabko.model.Match} DTO.
 */
@Component
@RequiredArgsConstructor
public class MatchConverter extends TwoWayConverter<Match, online.rabko.model.Match> {

    private final SeasonService seasonService;
    private final TeamService teamService;

    /**
     * {@inheritDoc}
     */
    @Override
    public online.rabko.model.Match convert(Match source) {
        return new online.rabko.model.Match()
            .id(source.getId())
            .seasonId(Objects.nonNull(source.getSeason()) ? source.getSeason().getId() : null)
            .date(source.getDate())
            .homeTeamId(Objects.nonNull(source.getHomeTeam()) ? source.getHomeTeam().getId() : null)
            .awayTeamId(Objects.nonNull(source.getAwayTeam()) ? source.getAwayTeam().getId() : null)
            .homeTeamScore(source.getHomeTeamScore())
            .awayTeamScore(source.getAwayTeamScore());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Match convertBack(online.rabko.model.Match dto) {
        Match match = Match.builder()
            .date(dto.getDate())
            .homeTeamScore(dto.getHomeTeamScore())
            .awayTeamScore(dto.getAwayTeamScore())
            .build();

        if (Objects.nonNull(dto.getSeasonId())) {
            match.setSeason(seasonService.findById(dto.getSeasonId()));
        }
        if (Objects.nonNull(dto.getHomeTeamId())) {
            match.setHomeTeam(teamService.findById(dto.getHomeTeamId()));
        }
        if (Objects.nonNull(dto.getAwayTeamId())) {
            match.setAwayTeam(teamService.findById(dto.getAwayTeamId()));
        }
        return match;
    }
}
