package online.rabko.basketball.controller.converter;

import online.rabko.basketball.entity.Season;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Season} entity and {@link online.rabko.model.Season} DTO.
 */
@Component
public class SeasonConverter extends TwoWayConverter<Season, online.rabko.model.Season> {

    /**
     * {@inheritDoc}
     */
    @Override
    public online.rabko.model.Season convert(Season source) {
        return new online.rabko.model.Season()
            .id(source.getId())
            .name(source.getName())
            .startDate(source.getStartDate())
            .endDate(source.getEndDate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Season convertBack(online.rabko.model.Season dto) {
        return Season.builder()
            .name(dto.getName())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .build();
    }
}
