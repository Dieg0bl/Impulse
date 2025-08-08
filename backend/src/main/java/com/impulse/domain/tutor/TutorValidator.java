package com.impulse.domain.tutor;

/**
 * Validador de Tutor.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 * Valida unicidad, formatos, rangos, relaciones y consentimientos.
 */
import java.util.List;
import java.util.regex.Pattern;

public class TutorValidator {

    /**
     * Valida un TutorDTO antes de operaciones críticas (registro, actualización).
     * Cumple compliance: RGPD, ISO 27001, ENS.
     * @param dto TutorDTO a validar
     * @throws IllegalArgumentException si alguna validación falla
     */
    public static void validar(TutorDTO dto) {
        if (dto == null) throw new IllegalArgumentException("TutorDTO no puede ser null");
        if (dto.getEmail() == null || dto.getEmail().isBlank())
            throw new IllegalArgumentException("El email es obligatorio");
        if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches())
            throw new IllegalArgumentException("Formato de email inválido");
        if (dto.getNombre() == null || dto.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        // usuariosIds puede ser null o vacío
        if (dto.getCreatedAt() == null)
            throw new IllegalArgumentException("createdAt es obligatorio");
        if (dto.getUpdatedAt() == null)
            throw new IllegalArgumentException("updatedAt es obligatorio");
        if (dto.getCreatedBy() == null || dto.getCreatedBy().isBlank())
            throw new IllegalArgumentException("createdBy es obligatorio");
        if (dto.getUpdatedBy() == null || dto.getUpdatedBy().isBlank())
            throw new IllegalArgumentException("updatedBy es obligatorio");
    }
    private TutorValidator() { throw new UnsupportedOperationException("Clase utilitaria"); }

    // Patrón de email seguro (RFC 5322 simplificado)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * Valida un tutor antes de persistir o actualizar.
     * @param tutor Tutor a validar
     * @param emailsExistentes Lista de emails ya registrados (para validar unicidad)
     * @param requiereConsentimientoEmail true si el tutor ha dado consentimiento para email
     * @throws IllegalArgumentException si alguna validación falla
     */
    public static void validate(Tutor tutor, List<String> emailsExistentes, boolean requiereConsentimientoEmail) {
        if (tutor == null) throw new IllegalArgumentException("Tutor no puede ser null");

        // Validación de email (público, requiere consentimiento)
        if (tutor.getEmail() == null || tutor.getEmail().isBlank())
            throw new IllegalArgumentException("El email es obligatorio");
        if (!EMAIL_PATTERN.matcher(tutor.getEmail()).matches())
            throw new IllegalArgumentException("Formato de email inválido");
        if (!requiereConsentimientoEmail)
            throw new IllegalArgumentException("Consentimiento para email no otorgado");
        if (emailsExistentes != null && emailsExistentes.contains(tutor.getEmail()))
            throw new IllegalArgumentException("El email ya está registrado");

        // Validación de nombre (público)
        if (tutor.getNombre() == null || tutor.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        // Validación de usuarios (relación N:M, público)
        if (tutor.getUsuarios() != null && tutor.getUsuarios().isEmpty())
            throw new IllegalArgumentException("Debe haber al menos un usuario si la lista no es null");

        // Validación de campos de auditoría (createdAt, updatedAt)
        if (tutor.getCreatedAt() == null)
            throw new IllegalArgumentException("createdAt es obligatorio");
        if (tutor.getUpdatedAt() == null)
            throw new IllegalArgumentException("updatedAt es obligatorio");
        if (tutor.getCreatedBy() == null || tutor.getCreatedBy().isBlank())
            throw new IllegalArgumentException("createdBy es obligatorio");
        if (tutor.getUpdatedBy() == null || tutor.getUpdatedBy().isBlank())
            throw new IllegalArgumentException("updatedBy es obligatorio");
        // deletedAt puede ser null (soft delete)
    }
}
