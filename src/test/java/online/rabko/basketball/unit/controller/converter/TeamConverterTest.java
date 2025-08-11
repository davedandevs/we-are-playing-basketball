package online.rabko.basketball.unit.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import online.rabko.basketball.controller.converter.TeamConverter;
import online.rabko.basketball.entity.Team;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TeamConverter}.
 */
class TeamConverterTest {

    private final TeamConverter converter = new TeamConverter();

    @Test
    void convert_shouldMapAllFields() {
        Team entity = Team.builder()
            .id(1L)
            .name("Warriors")
            .build();

        online.rabko.model.Team dto = converter.convert(entity);

        assertEquals(1L, dto.getId());
        assertEquals("Warriors", dto.getName());
    }

    @Test
    void convertBack_shouldMapAllFields() {
        online.rabko.model.Team dto = new online.rabko.model.Team()
            .id(2L)
            .name("Lakers");

        Team entity = converter.convertBack(dto);

        assertEquals(2L, entity.getId());
        assertEquals("Lakers", entity.getName());
    }
}
