package online.rabko.basketball.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.rabko.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entity representing an application user stored in the {@code users} table. Implements
 * {@link UserDetails} for integration with Spring Security.
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Unique identifier for the user (primary key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Unique username used for authentication.
     */
    @Column(name = "username", unique = true)
    private String username;

    /**
     * Encrypted user password.
     */
    @Column(name = "password", nullable = false)
    private String password;


    /**
     * User's first name.
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * User's last name.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * User role defining their access level in the system.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * Returns authorities granted to the user based on their role.
     *
     * @return a collection containing the user's role as a granted authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return password;
    }
}

