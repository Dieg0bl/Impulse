package com.impulse.lean.infrastructure.security;

import org.springframework.security.core.Authentication;

import com.impulse.lean.infrastructure.security.exception.JwtTokenException;

public interface JwtProvider {
    String generateToken(Authentication authentication);
    String generateTokenFromUsername(String username) throws JwtTokenException;
    String generateRefreshToken(String username) throws JwtTokenException;
    String getUsernameFromToken(String token) throws JwtTokenException;
    boolean validateToken(String token);
}
