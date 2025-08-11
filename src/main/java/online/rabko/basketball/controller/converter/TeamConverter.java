package online.rabko.basketball.controller.converter;

import online.rabko.basketball.entity.Team;
import org.springframework.stereotype.Component;

/**
 * Конвертер между сущностью Team и DTO online.rabko.model.Team.
 */
@Component
public class TeamConverter extends TwoWayConverter<Team, online.rabko.model.Team> {

    /**
     * {@inheritDoc}
     */
    @Override
    public online.rabko.model.Team convert(Team source) {
        return new online.rabko.model.Team()
            .id(source.getId())
            .name(source.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Team convertBack(online.rabko.model.Team source) {
        return Team.builder()
            .id(source.getId())
            .name(source.getName())
            .build();
    }
}
