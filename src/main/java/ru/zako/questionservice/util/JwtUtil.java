package ru.zako.questionservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.lang.NonNull;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import ru.zako.questionservice.user.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Component @Log
public class JwtUtil {
    private SecretKey JWT_SECRET = createSecretKey("s3cr3tK3yF0rJwt@2024Secure!@#1234567890"); // 32 символа

    // Храните в защищенном месте!
    private long JWT_EXPIRATION = TimeUnit.DAYS.toMillis(1);

    public SecretKey createSecretKey(String secret) {
        // Убедитесь, что строка достаточно длинная (например, 32 символа для HS256)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusDays(JWT_EXPIRATION).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(accessExpiration)
                .signWith(JWT_SECRET)
                .claim("roles", user.getRoles())
                .compact();
    }

    public String generateRefreshToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setExpiration(refreshExpiration)
                .signWith(JWT_SECRET)
                .compact();
    }

    public long getTokenUserId(String token) {
        return Long.parseLong(getClaims(token, JWT_SECRET).getSubject());
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, JWT_SECRET);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, JWT_SECRET);
    }

    protected boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.log(Level.INFO,"Token expired");
        } catch (UnsupportedJwtException unsEx) {
            log.log(Level.INFO,"Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            log.log(Level.INFO,"Malformed jwt");
        } catch (Exception e) {
            log.log(Level.INFO,"invalid token");
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, JWT_SECRET);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, JWT_SECRET);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}