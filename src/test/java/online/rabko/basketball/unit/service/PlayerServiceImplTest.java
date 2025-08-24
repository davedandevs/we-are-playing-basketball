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
import online.rabko.basketball.entity.Player;
import online.rabko.basketball.repository.PlayerRepository;
import online.rabko.basketball.service.PlayerService;
import online.rabko.basketball.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PlayerServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Test
    void findAll_shouldReturnList() {
        Player p1 = Player.builder().id(1L).firstName("A").build();
        Player p2 = Player.builder().id(2L).firstName("B").build();
        when(playerRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Player> result = playerService.findAll();

        assertEquals(2, result.size());
        assertEquals(p1, result.get(0));
        assertEquals(p2, result.get(1));
        verify(playerRepository).findAll();
    }

    @Test
    void findById_shouldReturnPlayer_whenExists() {
        Long id = 10L;
        Player existing = Player.builder().id(id).firstName("John").build();
        when(playerRepository.findById(id)).thenReturn(Optional.of(existing));

        Player result = playerService.findById(id);

        assertEquals(existing, result);
        verify(playerRepository).findById(id);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        Long id = 404L;
        when(playerRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> playerService.findById(id));
        assertEquals("Player not found with id: " + id, ex.getMessage());
    }

    @Test
    void create_shouldSave_whenIdIsNull() {
        Player toCreate = Player.builder().firstName("New").build();
        Player saved = Player.builder().id(1L).firstName("New").build();

        when(playerRepository.save(toCreate)).thenReturn(saved);

        Player result = playerService.create(toCreate);

        assertEquals(saved, result);
        verify(playerRepository, never()).existsById(any());
        verify(playerRepository).save(toCreate);
    }

    @Test
    void create_shouldSave_whenIdNotExists() {
        Player toCreate = Player.builder().id(5L).firstName("New").build();
        Player saved = Player.builder().id(5L).firstName("New").build();

        when(playerRepository.existsById(5L)).thenReturn(false);
        when(playerRepository.save(toCreate)).thenReturn(saved);

        Player result = playerService.create(toCreate);

        assertEquals(saved, result);
        verify(playerRepository).existsById(5L);
        verify(playerRepository).save(toCreate);
    }

    @Test
    void create_shouldThrow_whenIdExists() {
        Player toCreate = Player.builder().id(7L).firstName("Dup").build();
        when(playerRepository.existsById(7L)).thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
            () -> playerService.create(toCreate));
        assertEquals("Player already exists with id: 7", ex.getMessage());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void update_shouldSave_whenExists() {
        Long id = 8L;
        Player replacement = Player.builder().firstName("Upd").build();
        Player saved = Player.builder().id(id).firstName("Upd").build();

        when(playerRepository.existsById(id)).thenReturn(true);
        when(playerRepository.save(any(Player.class))).thenReturn(saved);

        Player result = playerService.update(id, replacement);

        assertEquals(saved, result);
        verify(playerRepository).existsById(id);
        verify(playerRepository).save(
            argThat(p -> p.getId().equals(id) && "Upd".equals(p.getFirstName())));
    }

    @Test
    void update_shouldThrow_whenNotExists() {
        Long id = 404L;
        Player replacement = Player.builder().firstName("X").build();

        when(playerRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> playerService.update(id, replacement));
        assertEquals("Player not found with id: " + id, ex.getMessage());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void delete_shouldDelete_whenExists() {
        Long id = 9L;
        when(playerRepository.existsById(id)).thenReturn(true);

        playerService.delete(id);

        verify(playerRepository).existsById(id);
        verify(playerRepository).deleteById(id);
    }

    @Test
    void delete_shouldThrow_whenNotExists() {
        Long id = 404L;
        when(playerRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> playerService.delete(id));
        assertEquals("Player not found with id: " + id, ex.getMessage());
        verify(playerRepository, never()).deleteById(any());
    }
}
