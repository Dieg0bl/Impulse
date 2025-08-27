package com.impulse.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.impulse.application.ports.ProceduresPort;

@ExtendWith(MockitoExtension.class)
class ErasureControllerTest {

    @Mock
    ProceduresPort procedures;

    @InjectMocks
    ErasureController controller;

    @Test
    void eraseUser_calls_procedure_and_returns_result(){
    var expected = java.util.Map.of("ok", true);
    when(procedures.insertAuditoriaAvanzada(org.mockito.ArgumentMatchers.any())).thenReturn(expected);

    ResponseEntity<Object> resp = controller.eraseUser(123L, "actor1");
    assertThat(resp.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
    assertThat(resp.getBody()).isEqualTo(expected);
        verify(procedures).callGdprErasure(123L, "actor1");
    }
}
