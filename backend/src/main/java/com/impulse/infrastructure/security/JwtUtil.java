package com.impulse.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
  private final Key key;
  private static final long EXP_MS = 1000L * 60 * 60 * 8; // 8h

  public JwtUtil(@Value("${jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(String subject) {
    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String validateAndGetSubject(String token) {
    Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    return jws.getBody().getSubject();
  }
}
