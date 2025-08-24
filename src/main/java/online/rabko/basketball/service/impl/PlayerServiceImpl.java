package online.rabko.basketball.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.Player;
import online.rabko.basketball.repository.PlayerRepository;
import online.rabko.basketball.service.PlayerService;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Player} entities.
 */
@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player findById(Long id) {
        return playerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Player not found with id: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player create(Player player) {
        if (Objects.nonNull(player.getId()) && playerRepository.existsById(player.getId())) {
            throw new EntityExistsException("Player already exists with id: " + player.getId());
        }
        return playerRepository.save(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player update(Long id, Player player) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException("Player not found with id: " + id);
        }
        player.setId(id);
        return playerRepository.save(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException("Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }
}
