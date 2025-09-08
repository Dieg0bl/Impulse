package com.impulse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FlagsCachingTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void conditionalGetReturns304WhenEtagMatches() throws Exception {
        var first = mockMvc.perform(get("/api/flags"))
                .andExpect(status().isOk())
                .andReturn();
        String etag = first.getResponse().getHeader(HttpHeaders.ETAG);
        assertThat(etag).isNotBlank();

        mockMvc.perform(get("/api/flags").header(HttpHeaders.IF_NONE_MATCH, etag))
                .andExpect(status().isNotModified());
    }
}
