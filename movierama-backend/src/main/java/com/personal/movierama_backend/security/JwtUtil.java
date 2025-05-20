package com.personal.movierama_backend.security;

import com.personal.movierama_backend.common.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    String secret;

    @Value("${jwt.expiration}")
    Long expiration;

    @PostConstruct
    public void init() {
        this.secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().minusSeconds(expiration)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
