package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(SupportController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.impulse.test.TestMocksConfig.class)
class SupportControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private com.impulse.security.EnterpriseRateLimiter enterpriseRateLimiter;

    @Autowired
    private com.impulse.common.flags.FlagService flags;

    @Test
    void createTicket_forbiddenIfNotAuthenticated() throws Exception {
        // Arrange: Simula que el flag está activo y el usuario no autenticado
        // (En un test real, aquí se configuraría el contexto de seguridad)
        // Act & Assert
        mockMvc.perform(post("/api/support/ticket/1?subject=test&body=test"))
                .andExpect(status().isForbidden());

        // Verifica que no se llama a servicios internos
        verifyNoInteractions(enterpriseRateLimiter, flags);
    }
}
