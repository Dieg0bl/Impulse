package com.impulse.application.ports;

public interface ProceduresPort {
    // Tipos y firmas mínimos usados por ErasureController
    Object insertAuditoriaAvanzada(Object params);
    void callGdprErasure(Long userId, String actor);
}
