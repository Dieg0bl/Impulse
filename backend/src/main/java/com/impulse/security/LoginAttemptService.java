package com.impulse.security;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Service to record login attempts and determine lockout status.
 * Lockout policy (initial): 5 failed attempts in rolling 10 minute window -> lock for 10 minutes.
 */
@Service
public class LoginAttemptService {

    private final JdbcTemplate jdbc;
    private static final int MAX_FAILURES = 5;
    private static final int WINDOW_MINUTES = 10;
    private static final int LOCK_MINUTES = 10;

    public LoginAttemptService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void record(String userIdentifier, String ip, boolean success){
        jdbc.update("INSERT INTO login_attempts(user_identifier, ip, success) VALUES (?,?,?)", userIdentifier, ip, success);
    }

    /**
     * Returns true if subject is currently locked. Lock condition: last failure window has >= MAX_FAILURES and last failure within lock window.
     */
    public boolean isLocked(String userIdentifier){
        // Count failures in window
        Integer failures = jdbc.queryForObject(
                "SELECT COUNT(*) FROM login_attempts WHERE user_identifier=? AND success=0 AND created_at > (NOW() - INTERVAL ? MINUTE)",
                Integer.class, userIdentifier, WINDOW_MINUTES);
        if(failures != null && failures >= MAX_FAILURES){
            // Get timestamp of most recent failure
            Instant lastFailure = jdbc.queryForObject(
                    "SELECT created_at FROM login_attempts WHERE user_identifier=? AND success=0 ORDER BY created_at DESC LIMIT 1",
                    (rs, i) -> rs.getTimestamp(1).toInstant(), userIdentifier);
            if(lastFailure != null){
                Duration since = Duration.between(lastFailure, Instant.now());
                return since.toMinutes() < LOCK_MINUTES;
            }
        }
        return false;
    }

    public long minutesUntilUnlock(String userIdentifier){
        Instant lastFailure = jdbc.query("SELECT created_at FROM login_attempts WHERE user_identifier=? AND success=0 ORDER BY created_at DESC LIMIT 1",
                ps -> ps.setString(1, userIdentifier), rs -> rs.next()? rs.getTimestamp(1).toInstant(): null);
        if(lastFailure==null) return 0;
        long elapsed = Duration.between(lastFailure, Instant.now()).toMinutes();
        long remaining = LOCK_MINUTES - elapsed;
        return remaining < 0 ? 0 : remaining;
    }

    public Map<String,Object> lockoutStatus(String userIdentifier){
        boolean locked = isLocked(userIdentifier);
        return Map.of("locked", locked, "minutes_until_unlock", locked? minutesUntilUnlock(userIdentifier):0);
    }
}
