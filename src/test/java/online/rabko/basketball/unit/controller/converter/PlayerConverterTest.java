package online.rabko.basketball.unit.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import online.rabko.basketball.controller.converter.PlayerConverter;
import online.rabko.basketball.entity.Player;
import online.rabko.basketball.entity.Team;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link PlayerConverter}.
 */
class PlayerConverterTest {

    private final PlayerConverter converter = new PlayerConverter();

    @Test
    void convert_shouldMapAllFields_withTeam() {
        Team team = Team.builder().id(7L).build();
        Player src = Player.builder()
            .id(100L)
            .team(team)
            .firstName("John")
            .lastName("Doe")
            .position("PG")
            .age(25)
            .height(190)
            .weight(85)
            .build();

        online.rabko.model.Player dto = converter.convert(src);

        assertEquals(100L, dto.getId());
        assertEquals(7L, dto.getTeamId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("PG", dto.getPosition());
        assertEquals(25, dto.getAge());
        assertEquals(190, dto.getHeight());
        assertEquals(85, dto.getWeight());
    }

    @Test
    void convert_shouldHandleNullTeam() {
        Player src = Player.builder()
            .id(101L)
            .team(null)
            .firstName("Jane")
            .lastName("Smith")
            .position("SF")
            .age(23)
            .height(185)
            .weight(75)
            .build();

        online.rabko.model.Player dto = converter.convert(src);

        assertEquals(101L, dto.getId());
        assertNull(dto.getTeamId());
        assertEquals("Jane", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("SF", dto.getPosition());
        assertEquals(23, dto.getAge());
        assertEquals(185, dto.getHeight());
        assertEquals(75, dto.getWeight());
    }

    @Test
    void convertBack_shouldMapAllFields_withoutTeam() {
        online.rabko.model.Player dto = new online.rabko.model.Player()
            .id(200L)
            .teamId(9L)
            .firstName("Mike")
            .lastName("Jordan")
            .position("SG")
            .age(30)
            .height(198)
            .weight(90);

        Player entity = converter.convertBack(dto);

        assertEquals(200L, entity.getId());
        assertNull(entity.getTeam());
        assertEquals("Mike", entity.getFirstName());
        assertEquals("Jordan", entity.getLastName());
        assertEquals("SG", entity.getPosition());
        assertEquals(30, entity.getAge());
        assertEquals(198, entity.getHeight());
        assertEquals(90, entity.getWeight());
    }
}
