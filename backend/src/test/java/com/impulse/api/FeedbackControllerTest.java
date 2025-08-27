package com.impulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedbackController.class)
@AutoConfigureMockMvc(addFilters = false)
class FeedbackControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void nps_notFoundIfFlagOff() throws Exception {
        mockMvc.perform(post("/api/feedback/nps/1?score=10"))
                .andExpect(status().isNotFound());
    }
}
