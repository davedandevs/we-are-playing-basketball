package online.rabko.basketball.repository;

import java.time.LocalDate;
import java.util.List;
import online.rabko.basketball.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Match} entities.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * Finds matches by date.
     *
     * @param date the match date.
     * @return a list of matches on the given date.
     */
    List<Match> findByDate(LocalDate date);
}
