package online.rabko.basketball.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import online.rabko.basketball.entity.Player;

/**
 * Service interface for managing {@link Player} entities.
 */
public interface PlayerService {

    /**
     * Retrieves all players.
     *
     * @return list of all players
     */
    List<Player> findAll();

    /**
     * Retrieves a player by ID.
     *
     * @param id the ID of the player
     * @return the found player
     * @throws EntityNotFoundException if no player with the given ID exists
     */
    Player findById(Long id);

    /**
     * Creates a new player.
     *
     * @param player the player to create
     * @return the created player
     * @throws EntityExistsException if the player already exists
     */
    Player create(Player player);

    /**
     * Updates an existing player.
     *
     * @param id     the ID of the player to update
     * @param player the updated player data
     * @return the updated player
     * @throws EntityNotFoundException if the player does not exist
     */
    Player update(Long id, Player player);

    /**
     * Deletes a player by ID.
     *
     * @param id the ID of the player to delete
     * @throws EntityNotFoundException if no player with the given ID exists
     */
    void delete(Long id);
}
