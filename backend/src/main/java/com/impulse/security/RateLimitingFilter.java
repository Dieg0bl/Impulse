package com.impulse.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Simple in-memory token bucket per IP+path (demo). */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class RateLimitingFilter extends OncePerRequestFilter {
    private static class Bucket { volatile int tokens; volatile long lastRefill; int capacity; int refillPerSec; }
    private final Map<String,Bucket> buckets = new ConcurrentHashMap<>();
    private Bucket bucket(String key){
        return buckets.computeIfAbsent(key, k->{
            Bucket b = new Bucket(); b.capacity=30; b.refillPerSec=30; b.tokens=30; b.lastRefill=System.currentTimeMillis(); return b;});
    }
    private void refill(Bucket b){
        long now = System.currentTimeMillis();
        long deltaMs = now - b.lastRefill; if(deltaMs<=0) return;
        int add = (int)(deltaMs/1000L)* b.refillPerSec;
        if(add>0){ b.tokens = Math.min(b.capacity, b.tokens+add); b.lastRefill = now; }
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        String key = ip+"|"+path;
        Bucket b = bucket(key);
        refill(b);
        if(b.tokens<=0){
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"rate_limited\",\"retry_after\":1}");
            return;
        }
        b.tokens--;
        filterChain.doFilter(request, response);
    }
}
