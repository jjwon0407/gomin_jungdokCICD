package com.gomin_jungdok.gdgoc.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {

    static final String BEARER_TOKEN = "Bearer ";
    static final long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60; // 1시간
    static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1000 시간

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }


    private final JwtUtil jwtUtil;

    //id 받아서 AccessToken 생성
    public AuthTokens generate(String id) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresAt = new Date(now + ACCESS_TOKEN_EXPIRATION_TIME);
        Date refreshTokenExpiresAt = new Date(now + REFRESH_TOKEN_EXPIRATION_TIME);

        String accessToken = jwtUtil.accessTokenGenerate(id, accessTokenExpiresAt);
        String refreshToken = jwtUtil.refreshTokenGenerate(id, refreshTokenExpiresAt);

        return AuthTokens.of(accessToken, refreshToken, BEARER_TOKEN, ACCESS_TOKEN_EXPIRATION_TIME);
    }
}
