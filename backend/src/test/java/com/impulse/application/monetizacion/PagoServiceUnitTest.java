package com.impulse.application.monetizacion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.impulse.application.ports.PagoPort;
import com.impulse.domain.monetizacion.Pago;
import com.impulse.domain.monetizacion.PagoDTO;

@ExtendWith(MockitoExtension.class)
class PagoServiceUnitTest {

    @Mock
    PagoPort pagoRepository;

    @InjectMocks
    PagoService pagoService;

    PagoDTO example;

    @BeforeEach
    void setUp(){
    example = new PagoDTO();
    example.setCantidad(9.99);
        example.setMoneda("EUR");
        example.setMetodo("STRIPE");
        example.setReferencia("ref-123");
    }

    @Test
    void crearPago_success(){
        when(pagoRepository.save(org.mockito.ArgumentMatchers.any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(99L);
            return p;
        });

        PagoDTO created = pagoService.crearPago(example);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(99L);
    }

    @Test
    void obtenerPago_not_found_throws(){
        when(pagoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pagoService.obtenerPago(1L)).isInstanceOf(com.impulse.common.exceptions.NotFoundException.class);
    }
}
