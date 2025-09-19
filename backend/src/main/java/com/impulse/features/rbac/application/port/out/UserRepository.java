package com.impulse.features.rbac.application.port.out;

import com.impulse.shared.annotations.Generated;

/**
 * Repository port: User persistence operations
 */
@Generated
public interface UserRepository {
    // TODO: Define repository operations - no JPA specifics

    com.impulse.features.rbac.domain.User save(com.impulse.features.rbac.domain.User user);

    java.util.Optional<com.impulse.features.rbac.domain.User> findByEmail(String email);
}
