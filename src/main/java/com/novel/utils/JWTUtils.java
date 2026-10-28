package com.novel.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtils {

    // HS256 至少需要 32 字节密钥
    private static final String SECRET =
            "my-novel-jwt-secret-key-2026-123456";

    // 14 天，单位为秒
    private static final long EXPIRATION = 1209600L;

    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8)
    );

    public static String createToken(String userId) {
        Date now = new Date();
        Date expireTime = new Date(
                now.getTime() + EXPIRATION * 1000
        );

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(KEY)
                .compact();
    }

    public static String parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}