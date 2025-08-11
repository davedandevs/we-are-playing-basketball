package online.rabko.basketball.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.Player;
import online.rabko.basketball.repository.PlayerRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Player} entities.
 */
@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * Retrieves all players.
     *
     * @return list of all players
     */
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    /**
     * Retrieves a player by ID.
     *
     * @param id the ID of the player
     * @return the found player
     * @throws EntityNotFoundException if no player with the given ID exists
     */
    public Player findById(Long id) {
        return playerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Player not found with id: " + id));
    }

    /**
     * Creates a new player.
     *
     * @param player the player to create
     * @return the created player
     * @throws EntityExistsException if the player already exists
     */
    public Player create(Player player) {
        if (Objects.nonNull(player.getId()) && playerRepository.existsById(player.getId())) {
            throw new EntityExistsException("Player already exists with id: " + player.getId());
        }
        return playerRepository.save(player);
    }

    /**
     * Updates an existing player.
     *
     * @param id     the ID of the player to update
     * @param player the updated player data
     * @return the updated player
     * @throws EntityNotFoundException if the player does not exist
     */
    public Player update(Long id, Player player) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException("Player not found with id: " + id);
        }
        player.setId(id);
        return playerRepository.save(player);
    }

    /**
     * Deletes a player by ID.
     *
     * @param id the ID of the player to delete
     * @throws EntityNotFoundException if no player with the given ID exists
     */
    public void delete(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException("Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }
}
