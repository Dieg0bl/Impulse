
package com.impulse.domain.usuario;

import java.time.Instant;

import org.hibernate.annotations.SQLDelete;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.impulse.domain.security.pii.PII;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidad Usuario. Cumple compliance: RGPD, ISO 27001, ENS.
 * Incluye anotaciones PII, enums, campos de auditoría y estructura JPA moderna.
 */
@Entity
@Table(name = "Usuario")
@SQLDelete(sql = "UPDATE Usuario SET deleted_at = NOW(3), estado = 'ELIMINADO' WHERE id = ?")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Usuario {

    public enum Estado { ACTIVO, INACTIVO, SUSPENDIDO, ELIMINADO }
    public enum Visibility { PRIVATE, VALIDATORS, PUBLIC }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email @NotBlank
    @Column(length=254, nullable=false, unique=true)
    @PII(PII.Level.HIGH)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="password_hash", length=60, nullable=false)
    private String passwordHash;

    // Compatibilidad: campo roles (cadena simple) utilizado por servicios y DTOs.
    @Column(name = "roles", length = 255)
    private String roles;

    // Compatibilidad: fechaNacimiento como Instant para alinear DTOs y servicios.
    @Column(name = "fecha_nacimiento")
    private Instant fechaNacimiento;

    @NotBlank @Size(max=100) @PII(PII.Level.LOW)
    private String nombre;

    @Size(max=150) @PII(PII.Level.LOW)
    private String apellidos;

    @Size(max=512) @Column(name="photo_url") private String photoUrl;
    @Size(max=32)  private String phone;

    @NotBlank @Size(max=16) private String locale = "es-ES";
    @NotBlank @Size(max=64) private String timezone = "Europe/Madrid";

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=10)
    private Estado estado = Estado.ACTIVO;

    @Enumerated(EnumType.STRING) @Column(name="default_visibility", nullable=false, length=12)
    private Visibility defaultVisibility = Visibility.PRIVATE;

    @Column(name="marketing_opt_in", nullable=false)
    private boolean marketingOptIn;

    @Column(name="verified_at") private Instant verifiedAt;

    @Column(name="created_at", nullable=false)  private Instant createdAt;
    @Column(name="updated_at", nullable=false)  private Instant updatedAt;
    @Column(name="deleted_at")                  private Instant deletedAt;

    // Auditoría/compatibilidad adicional
    @Column(name = "created_by", length = 128)
    private String createdBy;
    @Column(name = "updated_by", length = 128)
    private String updatedBy;

    @Transient
    private java.util.List<Long> validadores;

    @Column(name = "consentimiento_aceptado")
    private boolean consentimientoAceptado;

    @Version private Long version;

    @PrePersist void prePersist(){ var now = Instant.now(); createdAt = now; updatedAt = now; }
    @PreUpdate  void preUpdate(){ updatedAt = Instant.now(); }

    // Getters y setters estándar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    // Compatibilidad con API existente que usa getPassword()/setPassword()
    public String getPassword() { return this.passwordHash; }
    public void setPassword(String password) { this.passwordHash = password; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Visibility getDefaultVisibility() { return defaultVisibility; }
    public void setDefaultVisibility(Visibility defaultVisibility) { this.defaultVisibility = defaultVisibility; }
    public boolean isMarketingOptIn() { return marketingOptIn; }
    public void setMarketingOptIn(boolean marketingOptIn) { this.marketingOptIn = marketingOptIn; }
    public Instant getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; }
    public Instant getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Instant fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    public boolean isConsentimientoAceptado() { return consentimientoAceptado; }
    public void setConsentimientoAceptado(boolean consentimientoAceptado) { this.consentimientoAceptado = consentimientoAceptado; }

    public java.util.List<Long> getValidadores() { return validadores; }
    public void setValidadores(java.util.List<Long> validadores) { this.validadores = validadores; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
