package com.impulse.api;

import com.impulse.domain.User;
import com.impulse.infrastructure.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {
  private final UserRepository repo;
  public UserController(UserRepository repo) { this.repo = repo; }

  @GetMapping
  public List<User> getUsuarios() { return repo.findAll(); }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User crearUsuario(@RequestBody User user) { return repo.save(user); }
}
