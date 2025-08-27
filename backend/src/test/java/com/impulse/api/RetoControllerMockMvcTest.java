package com.impulse.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.impulse.application.reto.RetoService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = com.impulse.api.reto.RetoController.class)
class RetoControllerMockMvcTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RetoService retoService;

    @BeforeEach
    void setUp(){
        when(retoService.listarRetos()).thenReturn(java.util.List.of());
    }

    @Test
    void listRetos_returns_ok(){
        try{
            mvc.perform(get("/api/reto"))
                .andExpect(status().isOk());
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
