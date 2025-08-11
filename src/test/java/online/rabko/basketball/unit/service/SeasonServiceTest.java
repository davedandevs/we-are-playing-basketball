package online.rabko.basketball.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import online.rabko.basketball.entity.Season;
import online.rabko.basketball.repository.SeasonRepository;
import online.rabko.basketball.service.SeasonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link SeasonService}.
 */
@ExtendWith(MockitoExtension.class)
class SeasonServiceTest {

    @Mock
    private SeasonRepository seasonRepository;

    @InjectMocks
    private SeasonService seasonService;

    @Test
    void findAll_shouldReturnList() {
        Season s1 = Season.builder().id(1L).name("S1").build();
        Season s2 = Season.builder().id(2L).name("S2").build();
        when(seasonRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Season> result = seasonService.findAll();

        assertEquals(2, result.size());
        assertEquals(s1, result.get(0));
        assertEquals(s2, result.get(1));
        verify(seasonRepository).findAll();
    }

    @Test
    void findById_shouldReturnSeason_whenExists() {
        Long id = 10L;
        Season existing = Season.builder().id(id).name("S").build();
        when(seasonRepository.findById(id)).thenReturn(Optional.of(existing));

        Season result = seasonService.findById(id);

        assertEquals(existing, result);
        verify(seasonRepository).findById(id);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        Long id = 404L;
        when(seasonRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> seasonService.findById(id));
        assertEquals("Season not found: " + id, ex.getMessage());
    }

    @Test
    void create_shouldSave_whenValidAndUnique() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Season toCreate = Season.builder().name("S2025").startDate(start).endDate(end).build();
        Season saved = Season.builder().id(1L).name("S2025").startDate(start).endDate(end).build();

        when(seasonRepository.existsByNameIgnoreCase("S2025")).thenReturn(false);
        when(seasonRepository.overlaps(start, end, null)).thenReturn(false);
        when(seasonRepository.save(toCreate)).thenReturn(saved);

        Season result = seasonService.create(toCreate);

        assertEquals(saved, result);
        verify(seasonRepository).existsByNameIgnoreCase("S2025");
        verify(seasonRepository).overlaps(start, end, null);
        verify(seasonRepository).save(toCreate);
    }

    @Test
    void create_shouldThrow_whenNameExists() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Season toCreate = Season.builder().name("Dup").startDate(start).endDate(end).build();

        when(seasonRepository.existsByNameIgnoreCase("Dup")).thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
            () -> seasonService.create(toCreate));
        assertEquals("Season already exists: Dup", ex.getMessage());
        verify(seasonRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenDatesOverlap() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Season toCreate = Season.builder().name("S").startDate(start).endDate(end).build();

        when(seasonRepository.existsByNameIgnoreCase("S")).thenReturn(false);
        when(seasonRepository.overlaps(start, end, null)).thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
            () -> seasonService.create(toCreate));
        assertEquals("Season dates overlap with an existing season.", ex.getMessage());
        verify(seasonRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenDatesNull() {
        Season toCreate = Season.builder().name("S").build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> seasonService.create(toCreate));
        assertEquals("Season startDate and endDate are required.", ex.getMessage());
    }

    @Test
    void create_shouldThrow_whenEndBeforeStart() {
        LocalDate start = LocalDate.of(2025, 12, 31);
        LocalDate end = LocalDate.of(2025, 1, 1);
        Season toCreate = Season.builder().name("S").startDate(start).endDate(end).build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> seasonService.create(toCreate));
        assertEquals("Season endDate must not be before startDate.", ex.getMessage());
    }

    @Test
    void update_shouldSave_whenNameSameAsExisting() {
        Long id = 5L;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Season existing = Season.builder().id(id).name("Same").startDate(start).endDate(end)
            .build();
        Season replacement = Season.builder().name("Same").startDate(start).endDate(end).build();
        Season saved = Season.builder().id(id).name("Same").startDate(start).endDate(end).build();

        when(seasonRepository.findById(id)).thenReturn(Optional.of(existing));
        when(seasonRepository.existsByNameIgnoreCase("Same")).thenReturn(true);
        when(seasonRepository.overlaps(start, end, id)).thenReturn(false);
        when(seasonRepository.save(any(Season.class))).thenReturn(saved);

        Season result = seasonService.update(id, replacement);

        assertEquals(saved, result);
        verify(seasonRepository).findById(id);
        verify(seasonRepository).existsByNameIgnoreCase("Same");
        verify(seasonRepository).overlaps(start, end, id);
        verify(seasonRepository).save(
            argThat(s -> s.getId().equals(id) && s.getName().equals("Same")));
    }

    @Test
    void update_shouldSave_whenNameUnique() {
        Long id = 6L;
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        Season existing = Season.builder().id(id).name("Old").startDate(start).endDate(end).build();
        Season replacement = Season.builder().name("New").startDate(start).endDate(end).build();
        Season saved = Season.builder().id(id).name("New").startDate(start).endDate(end).build();

        when(seasonRepository.findById(id)).thenReturn(Optional.of(existing));
        when(seasonRepository.existsByNameIgnoreCase("New")).thenReturn(false);
        when(seasonRepository.overlaps(start, end, id)).thenReturn(false);
        when(seasonRepository.save(any(Season.class))).thenReturn(saved);

        Season result = seasonService.update(id, replacement);

        assertEquals(saved, result);
        verify(seasonRepository).findById(id);
        verify(seasonRepository).existsByNameIgnoreCase("New");
        verify(seasonRepository).overlaps(start, end, id);
        verify(seasonRepository).save(
            argThat(s -> s.getId().equals(id) && s.getName().equals("New")));
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        Long id = 404L;
        Season replacement = Season.builder().name("X").startDate(LocalDate.now())
            .endDate(LocalDate.now()).build();

        when(seasonRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> seasonService.update(id, replacement));
        assertEquals("Season not found: " + id, ex.getMessage());
    }

    @Test
    void update_shouldThrow_whenNameExistsAndDifferent() {
        Long id = 7L;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Season existing = Season.builder().id(id).name("Alpha").startDate(start).endDate(end)
            .build();
        Season replacement = Season.builder().name("Beta").startDate(start).endDate(end).build();

        when(seasonRepository.findById(id)).thenReturn(Optional.of(existing));
        when(seasonRepository.existsByNameIgnoreCase("Beta")).thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
            () -> seasonService.update(id, replacement));
        assertEquals("Season already exists: Beta", ex.getMessage());
        verify(seasonRepository, never()).save(any());
    }

    @Test
    void update_shouldThrow_whenDatesOverlap() {
        Long id = 8L;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        Season existing = Season.builder().id(id).name("S").startDate(start).endDate(end).build();
        Season replacement = Season.builder().name("S").startDate(start).endDate(end).build();

        when(seasonRepository.findById(id)).thenReturn(Optional.of(existing));
        when(seasonRepository.existsByNameIgnoreCase("S")).thenReturn(true);
        when(seasonRepository.overlaps(start, end, id)).thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
            () -> seasonService.update(id, replacement));
        assertEquals("Season dates overlap with an existing season.", ex.getMessage());
        verify(seasonRepository, never()).save(any());
    }

    @Test
    void update_shouldThrow_whenEndBeforeStart() {
        Long id = 9L;
        LocalDate start = LocalDate.of(2025, 12, 31);
        LocalDate end = LocalDate.of(2025, 1, 1);
        Season replacement = Season.builder().name("S").startDate(start).endDate(end).build();

        when(seasonRepository.findById(id)).thenReturn(
            Optional.of(Season.builder().id(id).name("Old").build()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> seasonService.update(id, replacement));
        assertEquals("Season endDate must not be before startDate.", ex.getMessage());
        verify(seasonRepository, never()).save(any());
    }

    @Test
    void update_shouldThrow_whenDatesNull() {
        Long id = 10L;
        Season replacement = Season.builder().name("S").build();

        when(seasonRepository.findById(id)).thenReturn(
            Optional.of(Season.builder().id(id).name("Old").build()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> seasonService.update(id, replacement));
        assertEquals("Season startDate and endDate are required.", ex.getMessage());
        verify(seasonRepository, never()).save(any());
    }

    @Test
    void delete_shouldDelete_whenExists() {
        Long id = 11L;
        when(seasonRepository.existsById(id)).thenReturn(true);

        seasonService.delete(id);

        verify(seasonRepository).existsById(id);
        verify(seasonRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        Long id = 404L;
        when(seasonRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> seasonService.delete(id));
        assertEquals("Season not found: " + id, ex.getMessage());
        verify(seasonRepository, never()).deleteById(any());
    }
}
