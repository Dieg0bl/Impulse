package com.impulse.auth.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitudes de registro empresarial con validaciones de seguridad.
 */
public class RegisterRequest {
    // Código de invitación opcional
    private String inviteCode;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmPassword;
    
    @AssertTrue(message = "Debe aceptar los términos y condiciones")
    private boolean aceptaTerminos;
    
    @AssertTrue(message = "Debe aceptar el tratamiento de datos personales")
    private boolean aceptaDatos;
    
    // Constructor vacío para Jackson
    public RegisterRequest() {}
    
    public RegisterRequest(String email, String nombre, String password, String confirmPassword, 
                          boolean aceptaTerminos, boolean aceptaDatos) {
        this.email = email;
        this.nombre = nombre;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.aceptaTerminos = aceptaTerminos;
        this.aceptaDatos = aceptaDatos;
    this.inviteCode = null;
    }
    
    // Validación personalizada para contraseñas coincidentes
    @AssertTrue(message = "Las contraseñas no coinciden")
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
    
    // Getters y Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public boolean isAceptaTerminos() {
        return aceptaTerminos;
    }
    
    public void setAceptaTerminos(boolean aceptaTerminos) {
        this.aceptaTerminos = aceptaTerminos;
    }
    
    public boolean isAceptaDatos() {
        return aceptaDatos;
    }
    
    public void setAceptaDatos(boolean aceptaDatos) {
        this.aceptaDatos = aceptaDatos;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
    
    @Override
    public String toString() {
    return "RegisterRequest{email='" + email + "', nombre='" + nombre + 
           "', password='[PROTECTED]', aceptaTerminos=" + aceptaTerminos + 
           ", aceptaDatos=" + aceptaDatos + ", inviteCode='" + inviteCode + "'}";
    }
}
