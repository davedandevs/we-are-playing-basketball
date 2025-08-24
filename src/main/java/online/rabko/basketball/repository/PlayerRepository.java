package online.rabko.basketball.repository;

import online.rabko.basketball.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Player} entities.
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

}
