package com.impulse.interfaces.rest.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para actualización de usuario
 */
public class UserUpdateDto {
    
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username solo puede contener letras, números, guiones y guiones bajos")
    private String username;
    
    @Email(message = "Email debe tener formato válido")
    private String email;
    
    @Size(min = 8, max = 100, message = "Password debe tener entre 8 y 100 caracteres")
    private String password;
    
    @Size(max = 100, message = "Nombre no puede exceder 100 caracteres")
    private String firstName;
    
    @Size(max = 100, message = "Apellido no puede exceder 100 caracteres")
    private String lastName;
    
    @Size(max = 500, message = "Bio no puede exceder 500 caracteres")
    private String bio;
    
    @Pattern(regexp = "^https?://.*", message = "Website debe ser una URL válida")
    private String website;
    
    @Size(max = 100, message = "Ubicación no puede exceder 100 caracteres")
    private String location;
    
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$", message = "Idioma debe tener formato ISO (ej: es, en-US)")
    private String preferredLanguage;
    
    @Pattern(regexp = "^[A-Za-z_/]+$", message = "Zona horaria debe tener formato válido")
    private String timeZone;
    
    private Boolean profilePublic;
    
    // Constructors
    public UserUpdateDto() {}
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    
    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    
    public Boolean getProfilePublic() { return profilePublic; }
    public void setProfilePublic(Boolean profilePublic) { this.profilePublic = profilePublic; }
}
