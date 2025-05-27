package com.petshop.petopia.security;

import com.petshop.petopia.model.user.User;
import com.petshop.petopia.repository.user.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${petopia.app.jwtSecret}")
    private String SECRET;

    @Value("${petopia.app.jwtExpirationMs}")
    private long jwtExpirationInMs;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String generateToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Attempted to generate token for null or empty email.");
            throw new IllegalArgumentException("Email cannot be null or empty for token generation.");
        }
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, email);
        redisTemplate.opsForValue().set("jwt:" + token, email, Duration.ofMillis(jwtExpirationInMs));
        return token;
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.trim().isEmpty()) {
            logger.debug("Attempted to extract email from null or empty token.");
            return null;
        }
        final Claims claims = extractAllClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public Integer extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Attempted to extract userId from null or empty token string (after Bearer check).");
            throw new IllegalArgumentException("Token string cannot be null or empty.");
        }

        String email = extractEmail(token);
        if (email == null) {
            logger.warn("Could not extract valid email from token {} to find user.", token);
            throw new IllegalArgumentException("Invalid token format or signature - cannot extract email.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found in DB for email extracted from token: {}", email);
                    return new RuntimeException("Không tìm thấy người dùng với email: " + email);
                });
        return user.getId();
    }

    public Date extractExpiration(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.debug("Attempted to extract expiration from null or empty token.");
            return null;
        }
        final Claims claims = extractAllClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        if (token == null || token.trim().isEmpty()) {
            logger.debug("Attempted to extract claim from null or empty token.");
            return null;
        }
        final Claims claims = extractAllClaims(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }

    private Claims extractAllClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.debug("Attempted to extract all claims from null or empty token.");
            return null;
        }
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.debug("JWT token is expired: {}", token);
            return null;
        } catch (MalformedJwtException e) {
            logger.warn("JWT token is malformed: {}", token);
            return null;
        } catch (SignatureException e) {
            logger.warn("JWT signature is invalid: {}", token);
            return null;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT token is illegal argument: {}", token);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error parsing JWT token: {}", token, e);
            return null;
        }
    }

    private Boolean isTokenExpired(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.debug("Attempted to check expiration for null or empty token.");
            return true;
        }
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        if (token == null || token.trim().isEmpty()) {
            logger.debug("Attempted to validate null or empty token.");
            return false;
        }
        if (userDetails == null) {
            logger.warn("Attempted to validate token with null UserDetails.");
            return false;
        }

        final String email = extractEmail(token);
        final Boolean expired = isTokenExpired(token);

        if (email == null || expired) {
            return false;
        }

        boolean isTokenInRedis = redisTemplate.hasKey("jwt:" + token);
        if (!isTokenInRedis) {
            logger.debug("Token not found in Redis: {}", token);
        }

        return (email.equals(userDetails.getUsername()) && isTokenInRedis);
    }

    public void invalidateToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Attempted to invalidate null or empty token string.");
            return;
        }
        redisTemplate.delete("jwt:" + token);
        logger.info("Token invalidated in Redis: {}", token);
    }
}