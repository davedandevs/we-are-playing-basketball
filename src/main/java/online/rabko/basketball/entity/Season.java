package online.rabko.basketball.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a  basketball season.
 */
@Entity
@Table(name = "seasons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Season {

    /**
     * Unique identifier for the season.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the season.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Start date of the season.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * End date of the season.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}
