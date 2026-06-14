package _LicHub.Backend.playerService.utilities;


import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import _LicHub.Backend.playerService.entities.Player;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JWTUtil {

    private final JWTSecretKeyValidator jwtSecretKeyValidator;
    private SecretKey key;

    public JWTUtil(JWTSecretKeyValidator jwtSecretKeyValidator) {
        this.jwtSecretKeyValidator = jwtSecretKeyValidator;
    }

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(
                    jwtSecretKeyValidator.getSecret().getBytes(StandardCharsets.UTF_8)
                );
    }
    public String generateToken(Player player) {

        long sevenDaysInMs = 604800000L;

        return Jwts.builder()
                .subject(player.getUserName())
                .claim("id", player.getId())
                .claim("role", player.getRole())
                .claim("email", player.getEmail())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + sevenDaysInMs)
                )
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractId(String token) {
        return extractClaims(token).get("id", Long.class);
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
}

