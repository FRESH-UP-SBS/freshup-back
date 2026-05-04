package com.cleaning.freshup.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {
        // .getBytes() 보다는 StandardCharsets.UTF_8을 명시하는 것이 정합성 면에서 좋음
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.refreshTokenExpiration = refreshTokenExpiration;

    }

    /*
     * JWT 토큰 생성
     */
    private String createToken(String providerId, long expirationTime, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // 최신 버전(0.12+) 방식의 빌더 패턴입니다.
        return Jwts.builder()
                .subject(providerId)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(String providerId) {
        return createToken(providerId, expiration, "access");
    }

    public String generateRefreshToken(String providerId) {
        return createToken(providerId, refreshTokenExpiration, "refresh");
    }

    public String getProviderId(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}