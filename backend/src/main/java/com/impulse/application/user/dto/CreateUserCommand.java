package com.impulse.application.user.dto;

import com.impulse.domain.enums.UserRole;

/**
 * CreateUserCommand - Comando para crear un nuevo usuario
 */
public class CreateUserCommand {

    private final String email;
    private final String name;
    private final String password;
    private final UserRole role;

    public CreateUserCommand(String email, String name, String password, UserRole role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
}
