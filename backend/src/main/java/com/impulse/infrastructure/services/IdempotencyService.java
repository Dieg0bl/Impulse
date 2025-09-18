package com.impulse.infrastructure.services;

import com.impulse.shared.utils.IdempotencyKey;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Service: IdempotencyService
 * Handles idempotency for request deduplication
 */
@Service
public class IdempotencyService {

    // In-memory storage for POC - should be replaced with Redis or database
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public <T> Optional<T> getResult(IdempotencyKey key, Class<T> resultType) {
        if (key == null) {
            return Optional.empty();
        }

        Object result = cache.get(key.getValue());
        if (result != null && resultType.isInstance(result)) {
            return Optional.of(resultType.cast(result));
        }

        return Optional.empty();
    }

    public <T> void storeResult(IdempotencyKey key, T result) {
        if (key != null && result != null) {
            cache.put(key.getValue(), result);
        }
    }

    public void remove(IdempotencyKey key) {
        if (key != null) {
            cache.remove(key.getValue());
        }
    }

    public void clearAll() {
        cache.clear();
    }
}
