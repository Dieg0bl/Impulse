package com.impulse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
  @Autowired MockMvc mvc;

  @Test
  void contextLoads() { }

  @Test
  void usuariosRequiresAuth() throws Exception {
    mvc.perform(get("/api/usuarios").accept(MediaType.APPLICATION_JSON))
       .andExpect(status().isUnauthorized());
  }
}
