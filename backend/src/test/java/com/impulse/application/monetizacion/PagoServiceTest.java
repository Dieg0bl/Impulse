package com.impulse.application.monetizacion;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.impulse.domain.monetizacion.Pago;
import com.impulse.domain.monetizacion.PagoDTO;
import com.impulse.application.ports.PagoPort;

/**
 * Test unitario para PagoService.
 * Cubre el caso básico de creación de pagos con validación de dependencias.
 * Cumple el estándar de cobertura mínima y plantilla para casos edge.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoPort pagoRepository;

    // AuditoriaService mock removed as it is not used in this test

    @InjectMocks
    private PagoService pagoService;

    @Test
    void testCrearPago() {
        // Given
        PagoDTO pagoDTO = new PagoDTO();
        Pago pago = new Pago();
        
        // When
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        PagoDTO resultado = pagoService.crearPago(pagoDTO);
        
        // Then
        assertNotNull(resultado);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }
}
