package online.rabko.basketball.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import online.rabko.basketball.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service for generating, parsing, and validating JWT tokens.
 */
@Service
public class JwtService {

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generates a JWT token for the given user.
     *
     * @param userDetails the authenticated user
     * @return signed JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUser) {
            claims.put("id", customUser.getId());
            claims.put("role", customUser.getRole());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Generates a JWT token with additional claims.
     *
     * @param extraClaims additional data to include in the token
     * @param userDetails the authenticated user
     * @return signed JWT token
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Date now = new Date(System.currentTimeMillis());
        long tokenValidityInSeconds = 3600;
        Date expiration = new Date(now.getTime() + tokenValidityInSeconds * 1000L);
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Validates whether the token is valid for the given user.
     *
     * @param token       the JWT token
     * @param userDetails the user to validate against
     * @return true if the token is valid and not expired
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            return isUserNameMatch(token, userDetails) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    /**
     * Checks if the username in the token matches the given user details.
     *
     * @param token the JWT token
     * @param userDetails the user details to compare with
     * @return true if usernames match
     */
    public boolean isUserNameMatch(String token, UserDetails userDetails) {
        return extractUserName(token).equals(userDetails.getUsername());
    }

    /**
     * Extracts a specific claim from the token using a resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver function to extract a specific claim
     * @param <T>            the claim type
     * @return the extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Checks whether the token has expired.
     *
     * @param token the JWT token
     * @return true if expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT token
     * @return expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parses the JWT token and extracts all claims.
     *
     * @param token the JWT token
     * @return all claims in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .setSigningKey(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Decodes and returns the signing key used to sign tokens.
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
