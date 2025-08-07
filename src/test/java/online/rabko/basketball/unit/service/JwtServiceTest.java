package online.rabko.basketball.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.service.JwtService;
import online.rabko.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for {@link JwtService}.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private final String jwtKey = "A7RjhH3kKJLusngyTPWbIZcTvWZTeJdTuMyY79pQccY=";

    private Key signingKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtKey));
    }

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", jwtKey);
    }

    @Test
    void generateToken_shouldReturnValidToken_forCustomUser() {
        User user = User.builder()
            .id(1L)
            .username("testuser")
            .password("password")
            .role(Role.USER)
            .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, user));
        assertEquals("testuser", jwtService.extractUserName(token));
    }

    @Test
    void generateToken_shouldReturnValidToken_forUserDetails() {
        UserDetails user = new org.springframework.security.core.userdetails.User(
            "basicUser", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, user));
        assertEquals("basicUser", jwtService.extractUserName(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUserNameMismatch() {
        User validUser = User.builder().username("valid").password("1").role(Role.USER).build();
        User fakeUser = User.builder().username("fake").password("2").role(Role.USER).build();

        String token = jwtService.generateToken(validUser);

        assertFalse(jwtService.isTokenValid(token, fakeUser));
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenExpired() {
        String expiredToken = Jwts.builder()
            .setSubject("expiredUser")
            .setIssuedAt(new Date(System.currentTimeMillis() - 10_000))
            .setExpiration(new Date(System.currentTimeMillis() - 1000))
            .signWith(signingKey(), SignatureAlgorithm.HS256)
            .compact();

        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("expiredUser");

        assertFalse(jwtService.isTokenValid(expiredToken, user));
    }

    @Test
    void isTokenExpired_shouldReturnTrue_whenTokenIsExpired() {
        String token = Jwts.builder()
            .setSubject("any")
            .setIssuedAt(new Date(System.currentTimeMillis() - 10_000))
            .setExpiration(new Date(System.currentTimeMillis() - 5_000))
            .signWith(signingKey(), SignatureAlgorithm.HS256)
            .compact();

        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void extractClaims_shouldReturnCorrectValues() {
        User user = User.builder()
            .id(42L)
            .username("admin")
            .password("s3cr3t")
            .role(Role.ADMIN)
            .build();

        String token = jwtService.generateToken(user);

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(signingKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

        assertEquals("admin", claims.getSubject());
        assertEquals(42, claims.get("id"));
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void isUserNameMatch_shouldReturnTrue_whenUsernameMatches() {
        UserDetails user = new org.springframework.security.core.userdetails.User(
            "matchingUser", "x", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isUserNameMatch(token, user));
    }

    @Test
    void isUserNameMatch_shouldReturnFalse_whenUsernameDifferent() {
        User user = User.builder()
            .id(3L)
            .username("realUser")
            .password("secret")
            .role(Role.USER)
            .build();

        String token = jwtService.generateToken(user);

        User another = User.builder()
            .id(4L)
            .username("notRealUser")
            .password("x")
            .role(Role.USER)
            .build();

        assertFalse(jwtService.isTokenValid(token, another));
    }
}
