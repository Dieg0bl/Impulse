package com.impulse.infrastructure.filters;

import com.impulse.shared.utils.CorrelationId;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Ensures X-Correlation-Id is present and propagated */
@Component
public class CorrelationIdFilter implements Filter {
    public static final String HEADER = "X-Correlation-Id";
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest) {
                HttpServletRequest req = (HttpServletRequest) request;
                String header = req.getHeader(HEADER);
                if (header == null || header.isBlank()) {
                    CorrelationId.generate();
                } else {
                    CorrelationId.set(header);
                }
            }
            chain.doFilter(request, response);
        } finally { CorrelationId.clear(); }
    }
}
