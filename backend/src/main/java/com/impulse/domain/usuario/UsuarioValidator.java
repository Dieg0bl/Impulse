
package com.impulse.domain.usuario;

// No es necesario el import, ya que está en el mismo paquete

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador de Usuario.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Valida unicidad, formatos, rangos, relaciones y consentimientos.
 */
public class UsuarioValidator {

    /**
     * Valida un UsuarioDTO antes de operaciones críticas (registro, actualización).
     * Cumple compliance: RGPD, ISO 27001, ENS.
     * @param dto UsuarioDTO a validar
     * @throws IllegalArgumentException si alguna validación falla
     */
    public static void validar(UsuarioDTO dto) {
        if (dto == null) throw new IllegalArgumentException("UsuarioDTO no puede ser null");
        
        validarCamposBasicosDTO(dto);
        validarConsentimientoDTO(dto);
        validarCamposAuditoriaDTO(dto);
    }
    
    /**
     * Valida campos básicos del UsuarioDTO.
     */
    private static void validarCamposBasicosDTO(UsuarioDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank())
            throw new IllegalArgumentException("El email es obligatorio");
        if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches())
            throw new IllegalArgumentException("Formato de email inválido");
        if (dto.getPassword() == null || dto.getPassword().length() < 10)
            throw new IllegalArgumentException("La contraseña debe tener al menos 10 caracteres");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
    // DTO fechaNacimiento is Instant now; compare with Instant.now()
    if (dto.getFechaNacimiento() != null && dto.getFechaNacimiento().isAfter(Instant.now()))
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
    if (dto.getEstado() == null || dto.getEstado().isBlank())
            throw new IllegalArgumentException("El estado es obligatorio");
    if (dto.getRoles() == null || dto.getRoles().isBlank())
            throw new IllegalArgumentException("El rol es obligatorio");
    }
    
    /**
     * Valida consentimiento del UsuarioDTO.
     */
    private static void validarConsentimientoDTO(UsuarioDTO dto) {
        if (!dto.isConsentimientoAceptado())
            throw new IllegalArgumentException("Consentimiento obligatorio no aceptado");
    }
    
    /**
     * Valida campos de auditoría del UsuarioDTO.
     */
    private static void validarCamposAuditoriaDTO(UsuarioDTO dto) {
        if (dto.getCreatedAt() == null)
            throw new IllegalArgumentException("createdAt es obligatorio");
        if (dto.getUpdatedAt() == null)
            throw new IllegalArgumentException("updatedAt es obligatorio");
        if (dto.getCreatedBy() == null || dto.getCreatedBy().isBlank())
            throw new IllegalArgumentException("createdBy es obligatorio");
        if (dto.getUpdatedBy() == null || dto.getUpdatedBy().isBlank())
            throw new IllegalArgumentException("updatedBy es obligatorio");
    }

    // Oculta el constructor público para evitar instanciación
    private UsuarioValidator() { throw new UnsupportedOperationException("Clase utilitaria"); }

    // Patrón de email seguro (RFC 5322 simplificado)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * Valida un usuario antes de persistir o actualizar.
     * @param usuario Usuario a validar
     * @param emailsExistentes Lista de emails ya registrados (para validar unicidad)
     * @param requiereConsentimientoEmail true si el usuario ha dado consentimiento para email
     * @param requiereConsentimientoFechaNacimiento true si el usuario ha dado consentimiento para fechaNacimiento
     * @throws IllegalArgumentException si alguna validación falla
     */
    public static void validate(Usuario usuario, List<String> emailsExistentes, boolean requiereConsentimientoEmail, boolean requiereConsentimientoFechaNacimiento) {
        if (usuario == null) throw new IllegalArgumentException("Usuario no puede ser null");

        validarEmail(usuario, emailsExistentes, requiereConsentimientoEmail);
        validarPassword(usuario);
        validarNombre(usuario);
        validarFechaNacimiento(usuario, requiereConsentimientoFechaNacimiento);
        validarEstado(usuario);
        validarRoles(usuario);
        validarValidadores(usuario);
        validarCamposAuditoria(usuario);
    }
    
    /**
     * Valida el email del usuario.
     */
    private static void validarEmail(Usuario usuario, List<String> emailsExistentes, boolean requiereConsentimientoEmail) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new IllegalArgumentException("El email es obligatorio");
        if (!EMAIL_PATTERN.matcher(usuario.getEmail()).matches())
            throw new IllegalArgumentException("Formato de email inválido");
        if (!requiereConsentimientoEmail)
            throw new IllegalArgumentException("Consentimiento para email no otorgado");
        if (emailsExistentes != null && emailsExistentes.contains(usuario.getEmail()))
            throw new IllegalArgumentException("El email ya está registrado");
    }
    
    /**
     * Valida la contraseña del usuario.
     */
    private static void validarPassword(Usuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().length() < 10)
            throw new IllegalArgumentException("La contraseña debe tener al menos 10 caracteres y estar cifrada (bcrypt)");
    }
    
    /**
     * Valida el nombre del usuario.
     */
    private static void validarNombre(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
    }
    
    /**
     * Valida la fecha de nacimiento del usuario.
     */
    private static void validarFechaNacimiento(Usuario usuario, boolean requiereConsentimientoFechaNacimiento) {
        if (usuario.getFechaNacimiento() != null && !requiereConsentimientoFechaNacimiento)
            throw new IllegalArgumentException("Consentimiento para fecha de nacimiento no otorgado");
    if (usuario.getFechaNacimiento() != null && usuario.getFechaNacimiento().isAfter(Instant.now()))
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
    }
    
    /**
     * Valida el estado del usuario.
     */
    private static void validarEstado(Usuario usuario) {
        if (usuario.getEstado() == null)
            throw new IllegalArgumentException("El estado es obligatorio");
        // Estado es un enum en la entidad; si se requiere validación adicional, añadirla aquí.
    }
    
    /**
     * Valida los roles del usuario.
     */
    private static void validarRoles(Usuario usuario) {
        if (usuario.getRoles() == null || usuario.getRoles().isBlank())
            throw new IllegalArgumentException("El rol es obligatorio");
    }
    
    /**
     * Valida los validadores del usuario.
     */
    private static void validarValidadores(Usuario usuario) {
        if (usuario.getValidadores() != null && usuario.getValidadores().isEmpty())
            throw new IllegalArgumentException("Debe haber al menos un validador si la lista no es null");
    }
    
    /**
     * Valida los campos de auditoría del usuario.
     */
    private static void validarCamposAuditoria(Usuario usuario) {
        if (usuario.getCreatedAt() == null)
            throw new IllegalArgumentException("createdAt es obligatorio");
        if (usuario.getUpdatedAt() == null)
            throw new IllegalArgumentException("updatedAt es obligatorio");
        if (usuario.getCreatedBy() == null || usuario.getCreatedBy().isBlank())
            throw new IllegalArgumentException("createdBy es obligatorio");
        if (usuario.getUpdatedBy() == null || usuario.getUpdatedBy().isBlank())
            throw new IllegalArgumentException("updatedBy es obligatorio");
        // deletedAt y deletedBy pueden ser null (soft delete)
    }

    // Métodos adicionales de validación granular pueden añadirse aquí
}
