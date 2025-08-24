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
import java.util.List;
import java.util.Optional;
import online.rabko.basketball.entity.Team;
import online.rabko.basketball.repository.TeamRepository;
import online.rabko.basketball.service.TeamService;
import online.rabko.basketball.service.impl.TeamServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link TeamServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    @Test
    void findAll_shouldReturnList() {
        Team t1 = Team.builder().id(1L).name("A").build();
        Team t2 = Team.builder().id(2L).name("B").build();
        when(teamRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Team> result = teamService.findAll();

        assertEquals(2, result.size());
        assertEquals(t1, result.get(0));
        assertEquals(t2, result.get(1));
        verify(teamRepository).findAll();
    }

    @Test
    void findById_shouldReturnTeam_whenExists() {
        Long id = 10L;
        Team existing = Team.builder().id(id).name("A").build();
        when(teamRepository.findById(id)).thenReturn(Optional.of(existing));

        Team result = teamService.findById(id);

        assertEquals(existing, result);
        verify(teamRepository).findById(id);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        Long id = 404L;
        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> teamService.findById(id));
        assertEquals("Team not found: " + id, ex.getMessage());
    }

    @Test
    void create_shouldSave_whenNameUnique() {
        Team toCreate = Team.builder().name("New").build();
        Team saved = Team.builder().id(1L).name("New").build();

        when(teamRepository.existsByNameIgnoreCase("New")).thenReturn(false);
        when(teamRepository.save(toCreate)).thenReturn(saved);

        Team result = teamService.create(toCreate);

        assertEquals(saved, result);
        verify(teamRepository).existsByNameIgnoreCase("New");
        verify(teamRepository).save(toCreate);
    }

    @Test
    void create_shouldThrow_whenNameExists() {
        Team toCreate = Team.builder().name("Dup").build();
        when(teamRepository.existsByNameIgnoreCase("Dup")).thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
            () -> teamService.create(toCreate));
        assertEquals("Team already exists: Dup", ex.getMessage());
    }

    @Test
    void update_shouldSave_whenNameSameAsExisting() {
        Long id = 5L;
        Team existing = Team.builder().id(id).name("Same").build();
        Team replacement = Team.builder().name("Same").build();
        Team saved = Team.builder().id(id).name("Same").build();

        when(teamRepository.findById(id)).thenReturn(Optional.of(existing));
        when(teamRepository.existsByNameIgnoreCase("Same")).thenReturn(
            true); // есть такая команда, но это мы же
        when(teamRepository.save(any(Team.class))).thenReturn(saved);

        Team result = teamService.update(id, replacement);

        assertEquals(saved, result);
        verify(teamRepository).findById(id);
        verify(teamRepository).existsByNameIgnoreCase("Same");
        verify(teamRepository).save(
            argThat(t -> t.getId().equals(id) && t.getName().equals("Same")));
    }

    @Test
    void update_shouldSave_whenNameUnique() {
        Long id = 6L;
        Team existing = Team.builder().id(id).name("Old").build();
        Team replacement = Team.builder().name("New").build();
        Team saved = Team.builder().id(id).name("New").build();

        when(teamRepository.findById(id)).thenReturn(Optional.of(existing));
        when(teamRepository.existsByNameIgnoreCase("New")).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(saved);

        Team result = teamService.update(id, replacement);

        assertEquals(saved, result);
        verify(teamRepository).findById(id);
        verify(teamRepository).existsByNameIgnoreCase("New");
        verify(teamRepository).save(
            argThat(t -> t.getId().equals(id) && t.getName().equals("New")));
    }

    @Test
    void update_shouldThrow_whenNameExistsAndDifferent() {
        Long id = 7L;
        Team existing = Team.builder().id(id).name("Alpha").build();
        Team replacement = Team.builder().name("Beta").build();

        when(teamRepository.findById(id)).thenReturn(Optional.of(existing));
        when(teamRepository.existsByNameIgnoreCase("Beta")).thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
            () -> teamService.update(id, replacement));
        assertEquals("Team already exists: Beta", ex.getMessage());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void update_shouldThrow_whenTeamNotFound() {
        Long id = 404L;
        Team replacement = Team.builder().name("Whatever").build();
        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> teamService.update(id, replacement));
        assertEquals("Team not found: " + id, ex.getMessage());
    }

    @Test
    void delete_shouldDelete_whenExists() {
        Long id = 9L;
        when(teamRepository.existsById(id)).thenReturn(true);

        teamService.delete(id);

        verify(teamRepository).existsById(id);
        verify(teamRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        Long id = 404L;
        when(teamRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> teamService.delete(id));
        assertEquals("Team not found: " + id, ex.getMessage());
        verify(teamRepository, never()).deleteById(any());
    }
}
