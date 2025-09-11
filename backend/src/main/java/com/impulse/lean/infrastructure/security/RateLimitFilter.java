package com.impulse.lean.infrastructure.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Component
public class RateLimitFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket(int capacity, int refillTokens, Duration period) {
        Refill refill = Refill.greedy(refillTokens, period);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String ip = request.getRemoteAddr();

    // Basic differentiation: auth endpoints have stricter limits
    boolean isAuth = req.getRequestURI().startsWith("/api/auth");
    final int capacity = isAuth ? 10 : 100;
    final int refill = isAuth ? 1 : 10;
    final Duration period = Duration.ofMinutes(1);

    String key = ip + ":" + (isAuth ? "auth" : "api");
    Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(capacity, refill, period));
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            res.setStatus(429);
            res.getWriter().write("Too Many Requests");
        }
    }
}
