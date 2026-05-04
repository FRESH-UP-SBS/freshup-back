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
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * JWT 토큰 생성 (email과 seq를 모두 담도록 수정)
     */
    private String createToken(String email, Long userSeq, long expirationTime, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(email) // Subject에 email 저장
                .claim("userSeq", userSeq) // Custom Claim에 seq 저장
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // Access Token 생성 (email과 seq 필요)
    public String generateAccessToken(String email, Long userSeq) {
        return createToken(email, userSeq, expiration, "access");
    }

    // Refresh Token 생성 (보통 Refresh는 email만 담아도 무방하지만 일관성을 위해 seq 포함 가능)
    public String generateRefreshToken(String email, Long userSeq) {
        return createToken(email, userSeq, refreshTokenExpiration, "refresh");
    }

    /**
     * 토큰에서 Email 추출
     */
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * 토큰에서 User Seq 추출 (최신 문법 적용)
     */
    public Long getSeq(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userSeq", Long.class);
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