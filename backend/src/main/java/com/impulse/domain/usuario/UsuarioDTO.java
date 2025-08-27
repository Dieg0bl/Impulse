package com.impulse.domain.usuario;



/**
 * DTO para transferencia de datos de Usuario.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Solo datos, sin lógica de negocio.
 */
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String avatar;
    private String rol;
    private String estado;
    private java.time.Instant fechaNacimiento;
    private boolean consentimientoAceptado;
    private java.time.LocalDateTime fechaRegistro;
    private java.time.LocalDateTime ultimoAcceso;
    private Configuracion configuracion;
    private Estadisticas estadisticas;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String inviteCode;
    // Added for authentication compatibility
    private String password;
    private java.util.Set<String> roles = new java.util.HashSet<>();

    public static class Configuracion {
        private boolean notificaciones;
        private String privacidad;
        private String idioma;

        public boolean isNotificaciones() { return notificaciones; }
        public void setNotificaciones(boolean notificaciones) { this.notificaciones = notificaciones; }
        public String getPrivacidad() { return privacidad; }
        public void setPrivacidad(String privacidad) { this.privacidad = privacidad; }
        public String getIdioma() { return idioma; }
        public void setIdioma(String idioma) { this.idioma = idioma; }
    }

    public static class Estadisticas {
        private int retosCompletados;
        private int puntosTotales;
        private String[] badges;
        private int racha;

        public int getRetosCompletados() { return retosCompletados; }
        public void setRetosCompletados(int retosCompletados) { this.retosCompletados = retosCompletados; }
        public int getPuntosTotales() { return puntosTotales; }
        public void setPuntosTotales(int puntosTotales) { this.puntosTotales = puntosTotales; }
        public String[] getBadges() { return badges; }
        public void setBadges(String[] badges) { this.badges = badges; }
        public int getRacha() { return racha; }
        public void setRacha(int racha) { this.racha = racha; }
    }

    /**
     * Constructor vacío necesario para frameworks de serialización/deserialización (por ejemplo, Jackson).
     * No realiza ninguna acción porque esta clase es un DTO puro sin lógica de negocio.
     */
    /**
     * Constructor vacío necesario para frameworks de serialización/deserialización (por ejemplo, Jackson).
     * No realiza ninguna acción porque esta clase es un DTO puro sin lógica de negocio.
     */
    public UsuarioDTO() {}

    // Getters y setters para todos los campos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public java.time.Instant getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(java.time.Instant fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public boolean isConsentimientoAceptado() { return consentimientoAceptado; }
    public void setConsentimientoAceptado(boolean consentimientoAceptado) { this.consentimientoAceptado = consentimientoAceptado; }
    public java.time.LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(java.time.LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public java.time.LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(java.time.LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    public Configuracion getConfiguracion() { return configuracion; }
    public void setConfiguracion(Configuracion configuracion) { this.configuracion = configuracion; }
    public Estadisticas getEstadisticas() { return estadisticas; }
    public void setEstadisticas(Estadisticas estadisticas) { this.estadisticas = estadisticas; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    // Compatibility accessors: string-based access used by some legacy callers.
    // Also provide Set-based accessors for newer code.
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    public String getRoles() {
        if (this.roles != null && !this.roles.isEmpty()) {
            return String.join(",", this.roles);
        }
        return this.rol;
    }

    public void setRoles(String roles) {
        this.rol = roles;
        if (roles == null || roles.isBlank()) {
            this.roles = new java.util.HashSet<>();
            return;
        }
        this.roles = java.util.Arrays.stream(roles.split(","))
            .map(String::trim)
            .collect(java.util.stream.Collectors.toSet());
    }

    public java.util.Set<String> getRolesSet() { return this.roles; }
    public void setRolesSet(java.util.Set<String> roles) { this.roles = roles; }
}
