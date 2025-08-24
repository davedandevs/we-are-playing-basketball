package online.rabko.basketball.repository;

import java.time.LocalDate;
import java.util.Optional;
import online.rabko.basketball.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Season} entities.
 */
@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    /**
     * Checks if a season exists with the given name (case-insensitive).
     *
     * @param name the season name to check.
     * @return true if a season exists with the given name, false otherwise.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Finds a season by its name (case-insensitive).
     *
     * @param name the season name.
     * @return an Optional containing the season if found, or empty otherwise.
     */
    Optional<Season> findByNameIgnoreCase(String name);

    /**
     * Checks whether any season overlaps with the given date range.
     *
     * @param start     the start date of the range.
     * @param end       the end date of the range.
     * @param excludeId a season ID to exclude from the check, or null.
     * @return true if an overlap exists, false otherwise.
     */
    @Query("""
        select (count(s) > 0) from Season s
        where (s.startDate <= :end and s.endDate >= :start)
          and (:excludeId is null or s.id <> :excludeId)
        """)
    boolean overlaps(LocalDate start, LocalDate end, Long excludeId);
}
