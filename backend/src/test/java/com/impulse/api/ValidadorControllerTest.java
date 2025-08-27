package com.impulse.api;

import com.impulse.application.usuario.ValidadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidadorControllerTest {
    @Mock
    private ValidadorService validadorService;

    @InjectMocks
    private ValidadorController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void invitarValidador_ok() {
        ValidadorController.ValidadorInvitacionRequest req = new ValidadorController.ValidadorInvitacionRequest();
        req.setEmail("test@correo.com");
        doNothing().when(validadorService).invitarValidador("test@correo.com");
        ResponseEntity<String> response = controller.invitarValidador(req);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Invitación enviada"));
    }

    @Test
    void invitarValidador_conflict() {
        ValidadorController.ValidadorInvitacionRequest req = new ValidadorController.ValidadorInvitacionRequest();
        req.setEmail("test@correo.com");
        doThrow(new com.impulse.common.exceptions.ConflictException("Ya invitado")).when(validadorService).invitarValidador("test@correo.com");
        ResponseEntity<String> response = controller.invitarValidador(req);
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Ya invitado", response.getBody());
    }

    @Test
    void eliminarValidador_ok() {
        doNothing().when(validadorService).eliminarValidador(1L);
        ResponseEntity<String> response = controller.eliminarValidador(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("eliminado"));
    }

    @Test
    void eliminarValidador_notFound() {
        doThrow(new com.impulse.common.exceptions.NotFoundException("No encontrado")).when(validadorService).eliminarValidador(2L);
        ResponseEntity<String> response = controller.eliminarValidador(2L);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("No encontrado", response.getBody());
    }
}
