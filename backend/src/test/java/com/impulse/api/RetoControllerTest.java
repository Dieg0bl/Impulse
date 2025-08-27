package com.impulse.api;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

import com.impulse.application.reto.RetoService;
import com.impulse.domain.reto.PermisoDTO;
import com.impulse.domain.reto.RetoDTO;

class RetoControllerTest {
    @Mock
    private RetoService retoService;

    @InjectMocks
    private RetoController controller;

    // Removed unused setUp method per static analysis

    @org.junit.jupiter.api.BeforeEach
    void initMocks(){
        org.mockito.MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPermisos_creadorPuedeEditar() {
        RetoDTO reto = new RetoDTO(
            1L, // id
            99L, // idCreador
            null, // idCategoria
            "titulo", // titulo
            "desc", // descripcion
            null, // fechaInicio
            null, // fechaFin
            null, // tipoValidacion
            null, // dificultad
            null, // esPublico
            null, // requiereEvidencia
            null, // tipoEvidencia
            null, // frecuenciaReporte
            null, // metaObjetivo
            null, // unidadMedida
            null, // valorObjetivo
            null, // estado
            null, // progreso
            java.util.Collections.emptyList(), // validadores
            java.util.Collections.emptyList(), // reportes
            null, // recompensas
            null, // configuracion
            null, // publicSlug
            null, // slaHorasValidacion
            null, // tipoConsecuencia
            null, // esPlantilla
            null, // visibility
            null, // fechaCreacion
            null, // fechaActualizacion
            null // updatedAt
        );
        when(retoService.obtenerReto(1L)).thenReturn(reto);
        Principal user = () -> "99";
        ResponseEntity<PermisoDTO> response = controller.getPermisos(1L, user);
    assertEquals(200, response.getStatusCode().value());
    PermisoDTO body = response.getBody();
    assertNotNull(body);
    assertTrue(body.isValor());
    }

    @Test
    void getPermisos_noCreadorNoPuedeEditar() {
        RetoDTO reto = new RetoDTO(
            1L, // id
            99L, // idCreador
            null, // idCategoria
            "titulo", // titulo
            "desc", // descripcion
            null, // fechaInicio
            null, // fechaFin
            null, // tipoValidacion
            null, // dificultad
            null, // esPublico
            null, // requiereEvidencia
            null, // tipoEvidencia
            null, // frecuenciaReporte
            null, // metaObjetivo
            null, // unidadMedida
            null, // valorObjetivo
            null, // estado
            null, // progreso
            java.util.Collections.emptyList(), // validadores
            java.util.Collections.emptyList(), // reportes
            null, // recompensas
            null, // configuracion
            null, // publicSlug
            null, // slaHorasValidacion
            null, // tipoConsecuencia
            null, // esPlantilla
            null, // visibility
            null, // fechaCreacion
            null, // fechaActualizacion
            null // updatedAt
        );
        when(retoService.obtenerReto(1L)).thenReturn(reto);
        Principal user = () -> "100";
        ResponseEntity<PermisoDTO> response = controller.getPermisos(1L, user);
    assertEquals(200, response.getStatusCode().value());
    PermisoDTO body = response.getBody();
    assertNotNull(body);
    assertFalse(body.isValor());
    }
}
