package online.rabko.basketball.unit.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import online.rabko.basketball.controller.converter.SeasonConverter;
import online.rabko.basketball.entity.Season;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SeasonConverter}.
 */
class SeasonConverterTest {

    private final SeasonConverter converter = new SeasonConverter();

    @Test
    void convert_shouldMapAllFields() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Season src = Season.builder()
            .id(100L)
            .name("Season 2025")
            .startDate(start)
            .endDate(end)
            .build();

        online.rabko.model.Season dto = converter.convert(src);

        assertEquals(100L, dto.getId());
        assertEquals("Season 2025", dto.getName());
        assertEquals(start, dto.getStartDate());
        assertEquals(end, dto.getEndDate());
    }

    @Test
    void convertBack_shouldMapAllFields_withoutId() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        online.rabko.model.Season dto = new online.rabko.model.Season()
            .id(999L)
            .name("Season 2026")
            .startDate(start)
            .endDate(end);

        Season entity = converter.convertBack(dto);

        assertNull(entity.getId());
        assertEquals("Season 2026", entity.getName());
        assertEquals(start, entity.getStartDate());
        assertEquals(end, entity.getEndDate());
    }
}
