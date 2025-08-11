package online.rabko.basketball.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a basketball player.
 */
@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    /**
     * Unique identifier for the player.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Team the player belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    /**
     * Player's first name.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Player's last name.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Player's position.
     */
    private String position;

    /**
     * Player's age.
     */
    private Integer age;

    /**
     * Player's height.
     */
    private Integer height;

    /**
     * Player's weight.
     */
    private Integer weight;
}
