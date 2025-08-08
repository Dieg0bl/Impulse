package com.impulse.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.impulse.domain.usuario.Usuario;

/**
 * Proveedor de auditoría para capturar automáticamente quién realiza operaciones.
 * Integra con Spring Data JPA @CreatedBy, @LastModifiedBy, etc.
 */
@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("system");
        }

        if (authentication.getPrincipal() instanceof Usuario user) {
            return Optional.of(user.getId().toString());
        }

        return Optional.of(authentication.getName());
    }
}
