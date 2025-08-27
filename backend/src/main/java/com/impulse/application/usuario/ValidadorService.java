package com.impulse.application.usuario;

import org.springframework.stereotype.Service;
import com.impulse.common.exceptions.ConflictException;
import com.impulse.common.exceptions.NotFoundException;
import java.util.HashSet;
import java.util.Set;

@Service
public class ValidadorService {
    private final Set<String> invitaciones = new HashSet<>();
    private final Set<Long> validadores = new HashSet<>();

    public void invitarValidador(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email es obligatorio");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (invitaciones.contains(email)) {
            throw new ConflictException("Ya invitado");
        }
        invitaciones.add(email);
        // Aquí se enviaría el email real
    }

    public void eliminarValidador(Long id) {
        if (!validadores.contains(id)) {
            throw new NotFoundException("Validador no encontrado");
        }
        validadores.remove(id);
    }
}
