package online.rabko.basketball.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import online.rabko.basketball.entity.User;
import online.rabko.basketball.enums.Role;
import online.rabko.basketball.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the {@link JwtService}.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private final String jwtKey = "A7RjhH3kKJLusngyTPWbIZcTvWZTeJdTuMyY79pQccY=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", jwtKey);
    }

    @Test
    void shouldGenerateAndValidateTokenSuccessfully() {
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
    void shouldFailValidationForWrongUser() {
        User user = User.builder()
            .id(1L)
            .username("testuser")
            .password("password")
            .role(Role.USER)
            .build();

        String token = jwtService.generateToken(user);

        User otherUser = User.builder()
            .id(2L)
            .username("hacker")
            .password("bad")
            .role(Role.USER)
            .build();

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void shouldExtractClaimsCorrectly() {
        User user = User.builder()
            .id(42L)
            .username("user42")
            .password("secret")
            .role(Role.ADMIN)
            .build();

        String token = jwtService.generateToken(user);

        Claims claims = Jwts.parser()
            .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtKey)))
            .build()
            .parseSignedClaims(token)
            .getPayload();

        assertEquals("user42", claims.getSubject());
        assertEquals(42, claims.get("id"));
        assertEquals("ADMIN", claims.get("role"));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void shouldGenerateTokenForNonCustomUser() {
        UserDetails basicUser = new org.springframework.security.core.userdetails.User(
            "basicUser",
            "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = jwtService.generateToken(basicUser);
        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, basicUser));
        assertEquals("basicUser", jwtService.extractUserName(token));
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotMatch() {
        User tokenOwner = User.builder()
            .id(1L)
            .username("realUser")
            .password("pass")
            .role(Role.USER)
            .build();

        String token = jwtService.generateToken(tokenOwner);

        User anotherUser = User.builder()
            .id(2L)
            .username("otherUser")
            .password("pass")
            .role(Role.USER)
            .build();

        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }

    @Test
    void shouldReturnFalseWhenTokenIsExpired() {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() - 1000);

        String token = Jwts.builder()
            .subject("expiredUser")
            .issuedAt(new Date(now.getTime() - 2000))
            .expiration(expiredAt)
            .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtKey)),
                SignatureAlgorithm.HS256)
            .compact();

        UserDetails user = new org.springframework.security.core.userdetails.User(
            "expiredUser",
            "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        assertFalse(jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldReturnFalseIfTokenExpiredViaHelper() {
        User user = User.builder()
            .id(3L)
            .username("expiredUser")
            .password("password")
            .role(Role.USER)
            .build();
        String expiredToken = Jwts.builder()
            .subject(user.getUsername())
            .issuedAt(new Date(System.currentTimeMillis() - 60000))
            .expiration(new Date(System.currentTimeMillis() - 1000))
            .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtKey)))
            .compact();

        assertFalse(jwtService.isTokenValid(expiredToken, user));
    }

    @Test
    void shouldReturnTrueWhenTokenIsExpiredHelper() {
        String expiredToken = Jwts.builder()
            .subject("expiredUser")
            .issuedAt(new Date(System.currentTimeMillis() - 10_000))
            .expiration(new Date(System.currentTimeMillis() - 5_000))
            .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtKey)))
            .compact();

        assertTrue(jwtService.isTokenExpired(expiredToken));
    }

    @Test
    void shouldReturnFalseIfExpiredUsernameDoesNotMatch() {
        String expiredToken = Jwts.builder()
            .subject("notMatchingUser")
            .issuedAt(new Date(System.currentTimeMillis() - 10_000))
            .expiration(new Date(System.currentTimeMillis() - 5_000))
            .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtKey)))
            .compact();

        User user = User.builder()
            .id(5L)
            .username("actualUser")
            .password("password")
            .role(Role.USER)
            .build();

        assertFalse(jwtService.isTokenValid(expiredToken, user));
    }

    @Test
    void shouldReturnTrueWhenUserNameMatches() {
        UserDetails user = new org.springframework.security.core.userdetails.User(
            "matchingUser",
            "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isUserNameMatch(token, user));
    }
}
