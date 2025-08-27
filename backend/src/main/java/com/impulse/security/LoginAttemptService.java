
package com.impulse.security;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
	public boolean isLocked(String email) { return false; }
	public int minutesUntilUnlock(String email) { return 0; }
	public void record(String email, boolean success) {}
}
