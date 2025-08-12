
package com.impulse.security;
import org.springframework.stereotype.Service;

@Service
public class SecurityAuditService {
	public void audit(Long userId, String ip, String action, String entity, String entityId, java.util.Map<String, Object> details, String severity) {}
}
