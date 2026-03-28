package com.example.demo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    // ✅ Secret key — must be at least 32 characters
    private final String SECRET = "myV3ryStr0ngS3cr3tK3y!@#$XYZ12345678";

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ✅ Generate token after login
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(getKey())
                .compact();
    }

    // ✅ Get email from token
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // ✅ Check if token is valid
    public boolean isTokenValid(String token) {
        try {
            extractEmail(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
