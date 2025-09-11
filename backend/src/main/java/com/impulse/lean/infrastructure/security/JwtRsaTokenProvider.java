package com.impulse.lean.infrastructure.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.impulse.lean.infrastructure.security.exception.JwtTokenException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component("rsaJwtProvider")
@Primary
public class JwtRsaTokenProvider implements JwtProvider {

    @Value("${impulse.security.jwt.rsa.private:}")
    private String rsaPrivatePem;

    @Value("${impulse.security.jwt.rsa.public:}")
    private String rsaPublicPem;

    @Value("${impulse.security.jwt.expiration:900000}")
    private long jwtExpirationMs;

    @Value("${impulse.security.jwt.refresh-expiration:1209600000}")
    private long refreshExpirationMs;

    // Cache an ephemeral keypair for dev when PEMs are not supplied
    private volatile PrivateKey cachedPrivateKey = null;
    private volatile PublicKey cachedPublicKey = null;

    private PrivateKey getPrivateKey() throws JwtTokenException {
        try {
            if (rsaPrivatePem != null && !rsaPrivatePem.isBlank()) {
                String pem = rsaPrivatePem.replaceAll("-----\\w+ PRIVATE KEY-----", "").replaceAll("\\s",""
                );
                byte[] keyBytes = Base64.getDecoder().decode(pem);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                return kf.generatePrivate(spec);
            }

            // fallback: generate ephemeral RSA keypair for dev/test
            if (cachedPrivateKey == null) {
                synchronized (this) {
                    if (cachedPrivateKey == null) {
                        java.security.KeyPairGenerator kpg = java.security.KeyPairGenerator.getInstance("RSA");
                        kpg.initialize(2048);
                        java.security.KeyPair kp = kpg.generateKeyPair();
                        cachedPrivateKey = kp.getPrivate();
                        cachedPublicKey = kp.getPublic();
                    }
                }
            }
            return cachedPrivateKey;
        } catch (Exception e) {
            throw new JwtTokenException("Failed to load private key", e);
        }
    }

    private PublicKey getPublicKey() throws JwtTokenException {
        try {
            if (rsaPublicPem != null && !rsaPublicPem.isBlank()) {
                String pem = rsaPublicPem.replaceAll("-----\\w+ PUBLIC KEY-----", "").replaceAll("\\s",""
                );
                byte[] keyBytes = Base64.getDecoder().decode(pem);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                return kf.generatePublic(spec);
            }

            // return ephemeral public key if generated
            if (cachedPublicKey != null) return cachedPublicKey;

            // ensure private generation creates public too
            getPrivateKey();
            return cachedPublicKey;
        } catch (Exception e) {
            throw new JwtTokenException("Failed to load public key", e);
        }
    }

    @Override
    public String generateToken(Authentication authentication) {
        try {
            String username = (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User)
                    ? ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername()
                    : (authentication != null ? String.valueOf(authentication.getPrincipal()) : "");
            return generateTokenFromUsername(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateTokenFromUsername(String username) throws JwtTokenException {
        Instant now = Instant.now();
        Instant exp = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(String username) throws JwtTokenException {
        Instant now = Instant.now();
        Instant exp = now.plus(refreshExpirationMs, ChronoUnit.MILLIS);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("type","refresh")
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) throws JwtTokenException {
        return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
