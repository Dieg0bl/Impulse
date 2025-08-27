package com.impulse.application.reto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.domain.reto.Reto;
import com.impulse.domain.reto.RetoDTO;

@ExtendWith(MockitoExtension.class)
class RetoServiceUnitTest {

    @Mock
    com.impulse.domain.reto.RetoRepositoryPort retoRepo;

    @Mock
    com.impulse.domain.reto.RetoValidator retoValidator;

    @Mock
    AuditoriaService auditoriaService;

    @Mock
    com.impulse.domain.reto.RetoMapper retoMapper;

    @InjectMocks
    RetoService retoService;

    RetoDTO dto = org.mockito.Mockito.mock(RetoDTO.class);
    Reto reto;

    @BeforeEach
    void setUp(){
        reto = new Reto();
        reto.setId(5L);
        reto.setTitulo("Prueba");
    org.mockito.Mockito.lenient().when(retoMapper.toEntity(org.mockito.ArgumentMatchers.any())).thenReturn(reto);
    org.mockito.Mockito.lenient().when(retoMapper.toDTO(reto)).thenReturn(dto);
    }

    @Test
    void crearReto_calls_repo_and_audit(){
        when(retoRepo.save(org.mockito.ArgumentMatchers.any(Reto.class))).thenAnswer(inv -> inv.getArgument(0));
        RetoDTO created = retoService.crearReto(dto);
        assertThat(created).isNotNull();
        verify(auditoriaService).registrarCreacionReto(reto.getId(), reto.getTitulo());
    }

    @Test
    void obtenerReto_throws_when_missing(){
        when(retoRepo.findById(10L)).thenReturn(Optional.empty());
        try{
            retoService.obtenerReto(10L);
            throw new AssertionError("Expected NotFoundException");
        } catch (com.impulse.common.exceptions.NotFoundException ex){
            // expected
        }
    }
}
