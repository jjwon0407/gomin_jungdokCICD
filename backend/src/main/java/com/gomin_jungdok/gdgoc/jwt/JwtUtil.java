package com.gomin_jungdok.gdgoc.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import static com.gomin_jungdok.gdgoc.jwt.AuthTokensGenerator.*;

@Component
public class JwtUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Key key;

    public JwtUtil(@Value("${custom.jwt.secretKey}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String accessTokenGenerate(String subject, Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)  // id
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String refreshTokenGenerate(String subject,Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)  // id
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public AuthTokens refreshAccessToken(String refreshToken) {
        System.out.println("1. Refresh Token 검증");
        Claims claims = parseClaims(refreshToken);
        String userId = claims.getSubject();

        if (userId == null) {
            throw new RuntimeException("유효하지 않은 refresh token입니다.");
        }

        System.out.println("2. AuthTokensGenerator 사용하여 Access Token 재발급");
        AuthTokens newTokens = generate(userId);

        return AuthTokens.of(newTokens.getAccessToken(), refreshToken, BEARER_TOKEN, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public AuthTokens generate(String id) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresAt = new Date(now + ACCESS_TOKEN_EXPIRATION_TIME);
        Date refreshTokenExpiresAt = new Date(now + REFRESH_TOKEN_EXPIRATION_TIME);

        String accessToken = accessTokenGenerate(id, accessTokenExpiresAt);
        String refreshToken = accessTokenGenerate(id, refreshTokenExpiresAt);

        return AuthTokens.of(accessToken, refreshToken, BEARER_TOKEN, ACCESS_TOKEN_EXPIRATION_TIME);
    }
}
